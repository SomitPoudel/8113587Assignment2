package au.edu.vu.artgallery.data

import au.edu.vu.artgallery.domain.ArtRepository
import au.edu.vu.artgallery.domain.Artwork
import au.edu.vu.artgallery.domain.Gallery
import javax.inject.Inject

// Implements ArtRepository using the API.
// Hilt supplies the ArtApi dependency through the constructor.
class NetworkArtRepository @Inject constructor(
    private val api: ArtApi
) : ArtRepository {

    // Send login details and return a nonblank keypass.
    // Throw an error if the keypass is missing or blank.
    override suspend fun login(username: String, password: String): String =
        api.login(LoginRequest(username, password)).keypass
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("The server returned an empty keypass.")

    override suspend fun dashboard(keypass: String): Gallery {

        // Request the data for the topic returned during login.
        val response = api.dashboard(keypass)

        // Missing data is treated as an error, not an empty collection.
        val entities = response.entities
            ?: throw IllegalStateException("Missing entities in response.")

        // Convert each JSON item into an Artwork object.
        return Gallery(
            entities.map { entity ->
                Artwork(
                    // Keep each field name and convert its value to text.
                    entity.entrySet().associate { (key, value) ->
                        key to when {
                            value.isJsonNull -> "Not provided"
                            value.isJsonPrimitive -> value.asString
                            else -> value.toString()
                        }
                    }
                )
            },

            // Use the number of received items if the total is missing.
            response.entityTotal ?: entities.size
        )
    }
}