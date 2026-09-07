package au.edu.vu.artgallery.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import au.edu.vu.artgallery.R
import au.edu.vu.artgallery.databinding.FragmentDashboardBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val model: DashboardViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentDashboardBinding.bind(view)
        val adapter = ArtworkAdapter { artwork ->
            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                findNavController().navigate(R.id.action_dashboard_details,
                    bundleOf("artwork" to Gson().toJson(artwork.fields)))
            }
        }
        binding.artworks.layoutManager = LinearLayoutManager(requireContext())
        binding.artworks.adapter = adapter
        binding.retry.setOnClickListener { model.load() }
        binding.logout.setOnClickListener { findNavController().navigate(R.id.action_logout) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    binding.progress.isVisible = state is UiState.Loading
                    binding.retry.isVisible = state is UiState.Error
                    binding.artworks.isVisible = state is UiState.Success
                    binding.status.text = when (state) {
                        is UiState.Error -> state.message
                        is UiState.Loading -> getString(R.string.loading)
                        is UiState.Success -> {
                            adapter.submitList(state.data.artworks)
                            if (state.data.artworks.isEmpty()) getString(R.string.empty)
                            else getString(R.string.count, state.data.artworks.size, state.data.total)
                        }
                        else -> ""
                    }
                }
            }
        }
    }
}
