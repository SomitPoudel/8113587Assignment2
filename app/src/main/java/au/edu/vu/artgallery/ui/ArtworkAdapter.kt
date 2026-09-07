package au.edu.vu.artgallery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import au.edu.vu.artgallery.databinding.ItemArtworkBinding
import au.edu.vu.artgallery.domain.Artwork

internal fun String.fieldLabel(): String = replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace('_', ' ').replaceFirstChar { it.uppercase() }

class ArtworkAdapter(private val onClick: (Artwork) -> Unit) :
    ListAdapter<Artwork, ArtworkAdapter.Holder>(object : DiffUtil.ItemCallback<Artwork>() {
        override fun areItemsTheSame(oldItem: Artwork, newItem: Artwork) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Artwork, newItem: Artwork) = oldItem == newItem
    }) {
    init { stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY }
    class Holder(val binding: ItemArtworkBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val artwork = getItem(position)
        holder.binding.title.text = artwork.title
        holder.binding.summary.text = artwork.summary.entries.joinToString("\n") { "${it.key.fieldLabel()}: ${it.value}" }
        holder.binding.root.setOnClickListener { onClick(artwork) }
    }
}
