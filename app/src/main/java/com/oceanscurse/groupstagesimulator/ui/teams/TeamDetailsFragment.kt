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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oceanscurse.groupstagesimulator.MainViewModel
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.databinding.FragmentTeamDetailsBinding
import com.oceanscurse.groupstagesimulator.model.Exceptions.NameEmptyException
import com.oceanscurse.groupstagesimulator.model.Exceptions.TeamEmptyException
import com.oceanscurse.groupstagesimulator.model.Player
import com.oceanscurse.groupstagesimulator.ui.teams.adapters.PlayersAdapter
import kotlinx.coroutines.launch
import java.lang.Exception

class TeamDetailsFragment : Fragment(), MenuProvider {

    companion object {
        const val ACTION_BAR_SAVE = 0
    }

    private var _binding: FragmentTeamDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mTeamDetailsViewModel: TeamDetailsViewModel
    private var mRecyclerViewData = mutableListOf<Player?>()

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
        val mainViewModel : MainViewModel by activityViewModels()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mTeamDetailsViewModel.setTeamId(mainViewModel.editingTeamId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mTeamDetailsViewModel.uiState.collect { uiState ->
                    binding.ivTeamImage.setImageResource(uiState.logoResourceId)

                    if (!binding.etvTeamName.hasFocus()) {
                        binding.etvTeamName.setText(uiState.teamName)
                    }

                    binding.btnReRoll.visibility = if (uiState.players.isEmpty()) View.GONE else View.VISIBLE
                    binding.btnAddPlayers.visibility = if (uiState.players.isEmpty()) View.VISIBLE else View.GONE

                    mRecyclerViewData.clear()
                    mRecyclerViewData.add(null) // Header
                    mRecyclerViewData.addAll(uiState.players)

                    binding.rvPlayers.adapter?.notifyDataSetChanged()

                    uiState.saveException?.let { handleException(it) }

                    if (uiState.isSaved) {
                        Navigation.findNavController(binding.root).popBackStack()
                    }
                }
            }
        }
    }

    private fun handleException(it: Exception) {
        val message = when (it) {
            is NameEmptyException -> getString(R.string.error_please_enter_team_name)
            is TeamEmptyException -> getString(R.string.error_please_setup_team_player)
            else -> getString(R.string.error_unknown_error)
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        mTeamDetailsViewModel.notifyExceptionHandled()
    }

    private fun setupLogoPicker() {
        binding.btnPreviousLogo.setOnClickListener {
            mTeamDetailsViewModel.selectPreviousLogo()
        }

        binding.btnNextLogo.setOnClickListener {
            mTeamDetailsViewModel.selectNextLogo()
        }

        binding.etvTeamName.addTextChangedListener {
            mTeamDetailsViewModel.updateTeamName(it?.toString() ?: "")
        }
    }

    private fun setupPlayerList() {
        binding.btnAddPlayers.setOnClickListener {
            mTeamDetailsViewModel.randomizePlayers()
        }

        binding.btnReRoll.setOnClickListener {
            mTeamDetailsViewModel.randomizePlayers()
        }

        val customAdapter = PlayersAdapter(mRecyclerViewData)
        binding.rvPlayers.apply {
            adapter = customAdapter
            layoutManager = LinearLayoutManager(context)
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
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == ACTION_BAR_SAVE) {
            mTeamDetailsViewModel.saveTeam()
        }
        return false
    }

}