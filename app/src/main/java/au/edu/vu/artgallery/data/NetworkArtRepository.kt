package au.edu.vu.artgallery.data

import au.edu.vu.artgallery.domain.ArtRepository
import au.edu.vu.artgallery.domain.Artwork
import au.edu.vu.artgallery.domain.Gallery
import javax.inject.Inject

class NetworkArtRepository @Inject constructor(private val api: ArtApi) : ArtRepository {
    override suspend fun login(username: String, password: String): String =
        api.login(LoginRequest(username, password)).keypass?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("The server returned an empty keypass.")

    override suspend fun dashboard(keypass: String): Gallery {
        val response = api.dashboard(keypass)
        val entities = response.entities ?: throw IllegalStateException("Missing entities in response.")
        return Gallery(entities.map { entity ->
            Artwork(entity.entrySet().associate { (key, value) ->
                key to when {
                    value.isJsonNull -> "Not provided"
                    value.isJsonPrimitive -> value.asString
                    else -> value.toString()
                }
            })
        }, response.entityTotal ?: entities.size)
    }
}
