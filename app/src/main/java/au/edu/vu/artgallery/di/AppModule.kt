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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun client(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun api(client: OkHttpClient): ArtApi = Retrofit.Builder()
        .baseUrl("https://nit3213apinew.onrender.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ArtApi::class.java)

    @Provides @Singleton
    fun repository(implementation: NetworkArtRepository): ArtRepository = implementation
}
