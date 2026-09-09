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

// Enable Hilt to provide the dependencies needed by this screen.
@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    // Get the ViewModel that manages dashboard data and loading state.
    private val model: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Access the dashboard views using View Binding.
        val binding = FragmentDashboardBinding.bind(view)

        // Handle artwork selection from the RecyclerView.
        val adapter = ArtworkAdapter { artwork ->

            // Only navigate if the dashboard is still the current screen.
            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {

                // Convert the selected artwork fields to JSON and pass them to Details.
                findNavController().navigate(
                    R.id.action_dashboard_details,
                    bundleOf("artwork" to Gson().toJson(artwork.fields))
                )
            }
        }

        // Display artwork cards in a vertical scrolling list.
        binding.artworks.layoutManager = LinearLayoutManager(requireContext())
        binding.artworks.adapter = adapter

        // Request the data again when the user taps Retry.
        binding.retry.setOnClickListener { model.load() }

        // Return to the login screen when the user signs out.
        binding.logout.setOnClickListener {
            findNavController().navigate(R.id.action_logout)
        }

        // Observe state while the fragment's view is at least STARTED.
        // Collection stops below STARTED and restarts when it becomes active again.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->

                    // Show the appropriate views for the current state.
                    binding.progress.isVisible = state is UiState.Loading
                    binding.retry.isVisible = state is UiState.Error
                    binding.artworks.isVisible = state is UiState.Success

                    // Update the status message and list when data arrives.
                    binding.status.text = when (state) {
                        is UiState.Error -> state.message

                        is UiState.Loading -> getString(R.string.loading)

                        is UiState.Success -> {
                            adapter.submitList(state.data.artworks)

                            // Show an empty message or the artwork count.
                            if (state.data.artworks.isEmpty()) {
                                getString(R.string.empty)
                            } else {
                                getString(
                                    R.string.count,
                                    state.data.artworks.size,
                                    state.data.total
                                )
                            }
                        }

                        else -> ""
                    }
                }
            }
        }
    }
}