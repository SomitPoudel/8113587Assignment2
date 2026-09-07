package au.edu.vu.artgallery.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.edu.vu.artgallery.R
import au.edu.vu.artgallery.databinding.FragmentDetailsBinding
import au.edu.vu.artgallery.domain.Artwork
import com.google.gson.JsonParser

class DetailsFragment : Fragment(R.layout.fragment_details) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentDetailsBinding.bind(view)
        val fields = JsonParser.parseString(requireArguments().getString("artwork") ?: "{}")
            .asJsonObject.entrySet().associate { it.key to it.value.asString }
        val artwork = Artwork(fields)
        binding.title.text = artwork.title
        binding.fields.text = artwork.fields.entries.joinToString("\n\n") {
            "${it.key.fieldLabel()}\n${it.value}"
        }
        binding.back.setOnClickListener { findNavController().navigateUp() }
    }
}
