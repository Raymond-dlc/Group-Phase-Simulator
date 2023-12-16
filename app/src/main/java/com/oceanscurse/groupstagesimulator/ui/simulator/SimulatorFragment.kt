package com.oceanscurse.groupstagesimulator.ui.simulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.oceanscurse.groupstagesimulator.MainActivity
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.databinding.FragmentSimulatorBinding
import kotlinx.coroutines.launch

class SimulatorFragment : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val mSimulatorViewModel: SimulatorViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        initEmptyState()

        return root
    }

    private fun initEmptyState() {
        binding.btnGoToTeams.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).openDrawer()
            }
        }
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mSimulatorViewModel.uiState.collect { uiState ->
                    binding.llEmptyState.visibility = if (uiState.meetsRequirements) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}