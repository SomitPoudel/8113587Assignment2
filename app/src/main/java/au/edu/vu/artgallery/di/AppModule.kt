package au.edu.vu.artgallery.di

import au.edu.vu.artgallery.data.ArtApi
import au.edu.vu.artgallery.data.NetworkArtRepository
import au.edu.vu.artgallery.domain.ArtRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// A Hilt module that provides dependencies for the application.
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide one shared HTTP client with network time limits.
    @Provides
    @Singleton
    fun client(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Time allowed to connect.
        .readTimeout(90, TimeUnit.SECONDS)   // Time allowed for a read operation.
        .callTimeout(120, TimeUnit.SECONDS)  // Time allowed for the entire request.
        .build()

    // Create the API service using the HTTP client supplied by Hilt.
    @Provides
    @Singleton
    fun api(client: OkHttpClient): ArtApi = Retrofit.Builder()
        .baseUrl("https://nit3213apinew.onrender.com/")
        .client(client)

        // Convert between JSON and the request/response models.
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ArtApi::class.java)

    // Supply NetworkArtRepository whenever ArtRepository is requested.
    @Provides
    @Singleton
    fun repository(
        implementation: NetworkArtRepository
    ): ArtRepository = implementation
}