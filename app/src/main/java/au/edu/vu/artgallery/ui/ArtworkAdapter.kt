package au.edu.vu.artgallery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import au.edu.vu.artgallery.databinding.ItemArtworkBinding
import au.edu.vu.artgallery.domain.Artwork

// Make API field names readable, for example "artworkTitle" becomes "Artwork Title".
internal fun String.fieldLabel(): String =
    replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }

// Connect the artwork list to RecyclerView cards.
// The onClick function lets the fragment handle the selected artwork.
class ArtworkAdapter(private val onClick: (Artwork) -> Unit) :
    ListAdapter<Artwork, ArtworkAdapter.Holder>(
        object : DiffUtil.ItemCallback<Artwork>() {

            // This implementation compares the full artwork objects.
            override fun areItemsTheSame(
                oldItem: Artwork,
                newItem: Artwork
            ) = oldItem == newItem

            // Check whether the displayed data has changed.
            override fun areContentsTheSame(
                oldItem: Artwork,
                newItem: Artwork
            ) = oldItem == newItem
        }
    ) {

    // Wait until the list has data before restoring its scroll position.
    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    // Hold references to the views inside one artwork card.
    class Holder(val binding: ItemArtworkBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Create a card from item_artwork.xml using View Binding.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemArtworkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    // Fill a card with the artwork at the given list position.
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val artwork = getItem(position)

        holder.binding.title.text = artwork.title

        // Show each summary field on a separate line.
        holder.binding.summary.text =
            artwork.summary.entries.joinToString("\n") {
                "${it.key.fieldLabel()}: ${it.value}"
            }

        // Pass the selected artwork back to the fragment when tapped.
        holder.binding.root.setOnClickListener { onClick(artwork) }
    }
}