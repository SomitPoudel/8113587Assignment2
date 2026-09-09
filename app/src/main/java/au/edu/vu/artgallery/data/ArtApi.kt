package au.edu.vu.artgallery.data

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Student ID and first name sent to the server during login.
data class LoginRequest(val username: String, val password: String)

// The server returns a keypass that identifies the user's topic.
data class LoginResponse(val keypass: String?)

// Dashboard response containing the items and their total count.
// JsonObject allows each item to contain different topic-specific fields.
data class DashboardResponse(
    val entities: List<JsonObject>?,
    val entityTotal: Int?
)

// Retrofit uses this interface to make requests to the API.
// Suspend functions allow requests without blocking the main thread.
interface ArtApi {

    // Send login details as a JSON body to the Sydney endpoint.
    @POST("sydney/auth")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Insert the returned keypass into the URL to fetch the topic's data.
    @GET("dashboard/{keypass}")
    suspend fun dashboard(@Path("keypass") keypass: String): DashboardResponse
}