package au.edu.vu.artgallery.domain

// Store all fields of an artwork as field names and text values.
data class Artwork(val fields: Map<String, String>) {

    // Exclude the description from the dashboard summary.
    val summary: Map<String, String>
        get() = fields.filterKeys { !it.equals("description", true) }

    // Find a suitable title using common field names.
    // If none exists, use the first summary value or "Artwork".
    val title: String
        get() = fields.entries.firstOrNull {
            it.key.lowercase() in setOf("title", "name", "artwork", "artworktitle")
        }?.value ?: summary.values.firstOrNull() ?: "Artwork"
}

// Store the artwork list and the total number of items.
data class Gallery(val artworks: List<Artwork>, val total: Int)

// Define the operations a repository must provide.
// This interface keeps the app separate from the network implementation.
interface ArtRepository {

    // Log in and return the topic keypass.
    suspend fun login(username: String, password: String): String

    // Load the gallery for the supplied keypass.
    suspend fun dashboard(keypass: String): Gallery
}