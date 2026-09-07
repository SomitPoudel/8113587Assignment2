package au.edu.vu.artgallery

import au.edu.vu.artgallery.data.*
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkArtRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: NetworkArtRepository
    @Before fun setup() {
        server = MockWebServer(); server.start()
        repository = NetworkArtRepository(Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build().create(ArtApi::class.java))
    }
    @After fun cleanup() { server.shutdown() }
    @Test fun `login posts correct JSON to Sydney endpoint`() = runTest {
        server.enqueue(MockResponse().setBody("""{"keypass":"art"}"""))
        assertEquals("art", repository.login("8113587", "FirstName"))
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("POST", request.method); assertEquals("/sydney/auth", request.path)
        val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals("8113587", json["username"].asString)
        assertEquals("FirstName", json["password"].asString)
    }
    @Test fun `dashboard preserves year and description from real art schema`() = runTest {
        server.enqueue(MockResponse().setBody("""{"entities":[{"artworkTitle":"Mona Lisa","artist":"Leonardo da Vinci","medium":"Oil paint","year":1503,"description":"Portrait"}],"entityTotal":1}"""))
        val gallery = repository.dashboard("art")
        assertEquals("/dashboard/art", server.takeRequest(1, TimeUnit.SECONDS)!!.path)
        assertEquals(1, gallery.total)
        assertEquals("1503", gallery.artworks.single().fields["year"])
        assertEquals("Portrait", gallery.artworks.single().fields["description"])
    }
    @Test fun `unauthorized response propagates HTTP error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401))
        try { repository.login("8113587", "Wrong"); fail("Expected HTTP failure") }
        catch (e: HttpException) { assertEquals(401, e.code()) }
    }
    @Test fun `blank keypass is rejected`() = runTest {
        server.enqueue(MockResponse().setBody("""{"keypass":""}"""))
        try { repository.login("8113587", "FirstName"); fail("Expected invalid response") }
        catch (_: IllegalStateException) { }
    }
    @Test fun `missing entities is rejected instead of shown as empty`() = runTest {
        server.enqueue(MockResponse().setBody("""{"entityTotal":7}"""))
        try { repository.dashboard("art"); fail("Expected invalid response") }
        catch (_: IllegalStateException) { }
    }
    @Test fun `empty collection is valid`() = runTest {
        server.enqueue(MockResponse().setBody("""{"entities":[],"entityTotal":0}"""))
        assertTrue(repository.dashboard("art").artworks.isEmpty())
    }
}
