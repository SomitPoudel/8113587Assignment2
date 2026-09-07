package au.edu.vu.artgallery.ui

import java.io.IOException
import retrofit2.HttpException

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

internal fun Throwable.userMessage(): String = when (this) {
    is HttpException -> when (code()) {
        400, 401, 403 -> "Request rejected. Check your student ID and case-sensitive first name."
        404 -> "The requested topic or endpoint could not be found."
        429 -> "Too many requests. Please wait and try again."
        else -> "The server could not complete the request. Please try again."
    }
    is IOException -> "Could not connect. Check your internet connection and try again."
    else -> "The response could not be loaded. Please try again."
}
