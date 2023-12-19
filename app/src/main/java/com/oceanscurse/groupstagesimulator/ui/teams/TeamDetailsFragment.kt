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
import com.oceanscurse.groupstagesimulator.model.exceptions.NameEmptyException
import com.oceanscurse.groupstagesimulator.model.exceptions.TeamEmptyException
import com.oceanscurse.groupstagesimulator.model.Player
import com.oceanscurse.groupstagesimulator.ui.teams.adapters.PlayersAdapter
import kotlinx.coroutines.launch

class TeamDetailsFragment : Fragment(), MenuProvider {

    companion object {
        /**
         * Identifier for the actionbar icon to save the team.
         */
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
        setupTeamInfo()
        setupPlayerList()
        return root
    }

    /**
     * Add this fragment as a MenuProvider to the menu host.
     */
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    /**
     * Sets up the viewModel and register for updates on the lifecycleScopes.
     *
     * On the start the viewModel is notified which team id is to be used.
     *
     * On each collection of the UiState the UI will:
     * - Update the team name if not being edited.
     * - Updates the visibility of the buttons.
     * - Shows the team strength if the players are already assigned.
     * - Update the recyclerViewData with the players.
     * - Handles an exception when there is one.
     * - Check if the Team has been saved, if so, dismisses this screen.
     */
    private fun setupViewModel() {
        mTeamDetailsViewModel = ViewModelProvider(this)[TeamDetailsViewModel::class.java]
        val mainViewModel: MainViewModel by activityViewModels()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mTeamDetailsViewModel.setTeamId(mainViewModel.editingTeamId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mTeamDetailsViewModel.uiState.collect { uiState ->
                    binding.ivTeamImage.setImageResource(uiState.logoResourceId)

                    // Only set the team name when the edit text is not focussed
                    // to prevent it from interfering with the user's typing.
                    if (!binding.etvTeamName.hasFocus()) {
                        binding.etvTeamName.setText(uiState.teamName)
                    }

                    binding.btnReRoll.visibility = if (uiState.players.isEmpty()) View.GONE else View.VISIBLE
                    binding.btnAddPlayers.visibility = if (uiState.players.isEmpty()) View.VISIBLE else View.GONE

                    // Add the total strength to the player label, if the team has players.
                    var playersLabel = getString(R.string.teams_players)
                    if (uiState.players.isNotEmpty()) {
                        playersLabel += " (${uiState.players.sumOf { it.strength + it.speed + it.defence }})"
                    }
                    binding.tvPlayers.text = playersLabel

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

    /**
     * Processes the exception that has been thrown by the viewModel.
     * Depending on the instance of the Exception a different message will show
     * in a snack bar. Once displayed, will notify the viewModel that it has
     * been handled.
     */
    private fun handleException(it: Exception) {
        val message = when (it) {
            is NameEmptyException -> getString(R.string.error_please_enter_team_name)
            is TeamEmptyException -> getString(R.string.error_please_setup_team_player)
            else -> getString(R.string.error_unknown_error)
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        mTeamDetailsViewModel.notifyExceptionHandled()
    }

    /**
     * Will set up everything related to the basic information of the team.
     *
     * Sets two onClickListeners to notify the viewModel to select the
     * previous or next logo.
     *
     * Adds a textChangedListener to the EditText to notify the viewModel
     * when the user is updating the team name.
     */
    private fun setupTeamInfo() {
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

    /**
     * Sets up the player list recyclerView and adapter.
     *
     * Additionally sets up two onClickListeners.
     * - A button for to add the players
     *      => Will notify the viewModel to randomize the players of the team.
     * - A button for to re-roll the players
     *      => Will notify the viewModel to randomize the players of the team.
     */
    private fun setupPlayerList() {
        val playersAdapter = PlayersAdapter(mRecyclerViewData)
        binding.rvPlayers.apply {
            adapter = playersAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.btnAddPlayers.setOnClickListener {
            mTeamDetailsViewModel.randomizePlayers()
        }

        binding.btnReRoll.setOnClickListener {
            mTeamDetailsViewModel.randomizePlayers()
        }
    }

    /**
     * Creates the menu. The team detail fragment only contains one menu item, that will allow the
     * user to attempt to save the team.
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, ACTION_BAR_SAVE, 0, getString(R.string.teams_action_add_team)).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
        }
    }

    /**
     * Handles action bar items.
     * ACTION_BAR_SAVE -> Will trigger the viewModel to attempt to save the team.
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == ACTION_BAR_SAVE) {
            mTeamDetailsViewModel.saveTeam()
        }
        return false
    }

    /**
     * Binding cleanup.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}