package au.edu.vu.artgallery.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.vu.artgallery.domain.ArtRepository
import au.edu.vu.artgallery.domain.Gallery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ArtRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val keypass: String = savedStateHandle["keypass"] ?: ""
    private val _state = MutableStateFlow<UiState<Gallery>>(UiState.Idle)
    val state = _state.asStateFlow()
    init { load() }

    fun load() {
        if (_state.value is UiState.Loading) return
        if (keypass.isBlank()) {
            _state.value = UiState.Error("Your session is missing. Please sign out and log in again.")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            try { _state.value = UiState.Success(repository.dashboard(keypass))
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { _state.value = UiState.Error(e.userMessage()) }
        }
    }
}
