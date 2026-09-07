package au.edu.vu.artgallery.data

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val keypass: String?)
data class DashboardResponse(val entities: List<JsonObject>?, val entityTotal: Int?)

interface ArtApi {
    @POST("sydney/auth")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @GET("dashboard/{keypass}")
    suspend fun dashboard(@Path("keypass") keypass: String): DashboardResponse
}
