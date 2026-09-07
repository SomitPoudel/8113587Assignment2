package au.edu.vu.artgallery.domain

/** Retains every API field; the assignment leaves topic-specific field names open. */
data class Artwork(val fields: Map<String, String>) {
    val summary: Map<String, String> get() = fields.filterKeys { !it.equals("description", true) }
    val title: String get() = fields.entries.firstOrNull {
        it.key.lowercase() in setOf("title", "name", "artwork", "artworktitle")
    }?.value ?: summary.values.firstOrNull() ?: "Artwork"
}
data class Gallery(val artworks: List<Artwork>, val total: Int)

interface ArtRepository {
    suspend fun login(username: String, password: String): String
    suspend fun dashboard(keypass: String): Gallery
}
