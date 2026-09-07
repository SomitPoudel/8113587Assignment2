package au.edu.vu.artgallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.vu.artgallery.domain.ArtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: ArtRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<String>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (_state.value is UiState.Loading) return
        val id = username.trim()
        // Accept the supplied seven-digit ID too; the brief's eight digits are an example.
        if (id.isEmpty() || !id.all { it in '0'..'9' } || password.isBlank()) {
            _state.value = UiState.Error("Enter your numeric student ID (without s) and first name.")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                _state.value = UiState.Success(repository.login(id, password))
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { _state.value = UiState.Error(e.userMessage()) }
        }
    }
}
