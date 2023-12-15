package com.oceanscurse.groupstagesimulator.ui.teams

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.databinding.FragmentTeamDetailsBinding
import kotlinx.coroutines.launch

class TeamDetailsFragment : Fragment(), MenuProvider {

    companion object {
        const val ACTION_BAR_SAVE = 0
    }

    private var _binding: FragmentTeamDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mTeamDetailsViewModel: TeamDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTeamDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMenu()
        setupViewModel()
        setupLogoPicker()
        setupPlayerList()
        return root
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupViewModel() {
        mTeamDetailsViewModel = ViewModelProvider(this)[TeamDetailsViewModel::class.java]
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mTeamDetailsViewModel.uiState.collect { uiState ->
                    binding.ivTeamImage.setImageResource(uiState.logoResourceId)
                    binding.etvTeamName.setText(uiState.teamName)

                    if (!uiState.isComplete) {

                    }
                }
            }
        }
    }

    private fun setupLogoPicker() {
        binding.btnNextLogo.setOnClickListener {
            mTeamDetailsViewModel.selectNextLogo()
        }

        binding.btnPreviousLogo.setOnClickListener {
            mTeamDetailsViewModel.selectPreviousLogo()
        }
    }

    private fun setupPlayerList() {
        binding.btnAddPlayers.setOnClickListener {
            mTeamDetailsViewModel.randomizePlayers()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, ACTION_BAR_SAVE, 0, getString(R.string.teams_action_add_team)).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_gallery)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == ACTION_BAR_SAVE) {
            mTeamDetailsViewModel.saveTeam(binding.etvTeamName.text.toString())
        }
        return false
    }

}