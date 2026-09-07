package au.edu.vu.artgallery.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import au.edu.vu.artgallery.R
import au.edu.vu.artgallery.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val model: LoginViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)
        fun submit() = model.login(binding.username.text.toString(), binding.password.text.toString())
        binding.login.setOnClickListener { submit() }
        binding.password.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { submit(); true } else false
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    binding.progress.isVisible = state is UiState.Loading
                    binding.login.isEnabled = state !is UiState.Loading
                    binding.username.isEnabled = state !is UiState.Loading
                    binding.password.isEnabled = state !is UiState.Loading
                    binding.error.isVisible = state is UiState.Error
                    binding.error.text = (state as? UiState.Error)?.message
                    if (state is UiState.Success && findNavController().currentDestination?.id == R.id.loginFragment) {
                        binding.password.text?.clear()
                        findNavController().navigate(R.id.action_login_dashboard, bundleOf("keypass" to state.data))
                    }
                }
            }
        }
    }
}
