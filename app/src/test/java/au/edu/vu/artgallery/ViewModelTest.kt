package au.edu.vu.artgallery

import androidx.lifecycle.SavedStateHandle
import au.edu.vu.artgallery.domain.*
import au.edu.vu.artgallery.ui.*
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun cleanup() { Dispatchers.resetMain() }

    private class FakeRepository : ArtRepository {
        var calls = 0
        var suppliedUsername = ""
        var suppliedPassword = ""
        var suppliedKeypass = ""
        var failure: Exception? = null
        var gallery = Gallery(listOf(Artwork(mapOf("artworkTitle" to "Mona Lisa"))), 1)
        override suspend fun login(username: String, password: String): String {
            calls++; suppliedUsername = username; suppliedPassword = password
            failure?.let { throw it }
            return "art"
        }
        override suspend fun dashboard(keypass: String): Gallery {
            calls++; suppliedKeypass = keypass
            failure?.let { throw it }
            return gallery
        }
    }
    @Test fun `invalid inputs never call repository`() = runTest {
        val repo = FakeRepository()
        val vm = LoginViewModel(repo)
        listOf("" to "FirstName", "s8113587" to "FirstName", "8113587" to "").forEach {
            vm.login(it.first, it.second)
            assertTrue(vm.state.value is UiState.Error)
        }
        advanceUntilIdle()
        assertEquals(0, repo.calls)
    }
    @Test fun `valid seven digit ID preserves password case and returns keypass`() = runTest {
        val repo = FakeRepository(); val vm = LoginViewModel(repo)
        vm.login(" 8113587 ", "FirstName")
        assertEquals(UiState.Loading, vm.state.value)
        advanceUntilIdle()
        assertEquals("8113587", repo.suppliedUsername)
        assertEquals("FirstName", repo.suppliedPassword)
        assertEquals(UiState.Success("art"), vm.state.value)
    }
    @Test fun `duplicate login while loading sends one request`() = runTest {
        val repo = FakeRepository(); val vm = LoginViewModel(repo)
        vm.login("8113587", "FirstName"); vm.login("8113587", "FirstName")
        advanceUntilIdle(); assertEquals(1, repo.calls)
    }
    @Test fun `network error is shown and login can retry`() = runTest {
        val repo = FakeRepository().apply { failure = IOException() }
        val vm = LoginViewModel(repo)
        vm.login("8113587", "FirstName"); advanceUntilIdle()
        assertTrue((vm.state.value as UiState.Error).message.contains("internet"))
        repo.failure = null
        vm.login("8113587", "FirstName"); advanceUntilIdle()
        assertEquals(UiState.Success("art"), vm.state.value)
    }
    @Test fun `dashboard forwards restored keypass and loads artworks`() = runTest {
        val repo = FakeRepository()
        val vm = DashboardViewModel(repo, SavedStateHandle(mapOf("keypass" to "art")))
        assertEquals(UiState.Loading, vm.state.value)
        advanceUntilIdle()
        assertEquals("art", repo.suppliedKeypass)
        assertEquals(UiState.Success(repo.gallery), vm.state.value)
    }
    @Test fun `dashboard error can retry to empty success`() = runTest {
        val repo = FakeRepository().apply { failure = IOException() }
        val vm = DashboardViewModel(repo, SavedStateHandle(mapOf("keypass" to "art")))
        advanceUntilIdle(); assertTrue(vm.state.value is UiState.Error)
        repo.failure = null; repo.gallery = Gallery(emptyList(), 0)
        vm.load(); advanceUntilIdle()
        assertEquals(UiState.Success(Gallery(emptyList(), 0)), vm.state.value)
    }
    @Test fun `missing session does not request dashboard`() = runTest {
        val repo = FakeRepository(); val vm = DashboardViewModel(repo, SavedStateHandle())
        advanceUntilIdle()
        assertTrue(vm.state.value is UiState.Error); assertEquals(0, repo.calls)
    }
    @Test fun `dashboard suppresses duplicate requests while loading`() = runTest {
        val repo = FakeRepository()
        val vm = DashboardViewModel(repo, SavedStateHandle(mapOf("keypass" to "art")))
        vm.load(); advanceUntilIdle(); assertEquals(1, repo.calls)
    }
    @Test fun `summary omits description but details retain it`() {
        val artwork = Artwork(linkedMapOf("artworkTitle" to "Mona Lisa", "artist" to "Leonardo", "description" to "Portrait"))
        assertEquals("Mona Lisa", artwork.title)
        assertFalse(artwork.summary.containsKey("description"))
        assertEquals("Portrait", artwork.fields["description"])
        assertEquals("Leonardo", artwork.summary["artist"])
    }
}
