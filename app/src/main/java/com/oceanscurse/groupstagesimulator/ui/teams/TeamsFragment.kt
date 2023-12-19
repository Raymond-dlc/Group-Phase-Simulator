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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.MainViewModel
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.databinding.FragmentTeamsBinding
import com.oceanscurse.groupstagesimulator.model.Team
import com.oceanscurse.groupstagesimulator.ui.teams.adapters.TeamsAdapter
import kotlinx.coroutines.launch

class TeamsFragment : Fragment(), MenuProvider {

    companion object {
        /**
         * Identifier for the actionbar icon to create the teams.
         */
        const val ACTION_CREATE_TEAMS = 0
    }

    private var _binding: FragmentTeamsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mTeamsViewModel: TeamsViewModel
    private var mRecyclerViewData = mutableListOf<Team>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTeamsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMenu()
        setupViewModel()
        setupTeams()
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
     * Everytime the screen resumes, the viewModel is asked to refresh the teams,
     * to make sure they are rendered.
     *
     * On each collection of the UiState the UI will:
     * - Update the recyclerViewData with the teams.
     * - If a team is filled, it will add the team.
     * - If a team is not filled, it will add a new team without name or players.
     */
    private fun setupViewModel() {
        mTeamsViewModel = ViewModelProvider(this)[TeamsViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mTeamsViewModel.uiState.collect {
                    mRecyclerViewData.clear()

                    for (i in 0 until Constants.NUM_COMPETING_TEAMS) {
                        val team = it.teams.find { team -> team.id == i }
                        if (team != null) {
                            mRecyclerViewData.add(team)
                        } else {
                            mRecyclerViewData.add(Team(i, "", R.drawable.logo_1, listOf()))
                        }
                    }

                    binding.rvTeams.adapter?.notifyDataSetChanged()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mTeamsViewModel.refreshTeams()
            }
        }
    }

    /**
     * Sets up the teams recyclerView and adapter. The recyclerView has a grid layout manager
     * to more easily space out the teams in the center. When clicking on a team, the user
     * will be navigated to the details screen. The MainViewModel is referenced here to aid
     * in sending the teamId over to the details screen.
     */
    private fun setupTeams() {
        val teamsAdapter = TeamsAdapter(mRecyclerViewData) { team ->
            val mainViewModel: MainViewModel by activityViewModels()
            mainViewModel.editingTeamId = team.id
            Navigation.findNavController(binding.root).navigate(R.id.nav_team_details)
        }

        binding.rvTeams.apply {
            adapter = teamsAdapter
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    /**
     * Creates the menu. The teams fragment only contains one menu item, that will allow the
     * user to quickly setup all teams, or shuffle them.
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, ACTION_CREATE_TEAMS, 0, getString(R.string.teams_add_team)).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh)
        }
    }

    /**
     * Handles action bar items.
     * ACTION_CREATE_TEAMS -> Will trigger the viewModel to create teams.
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == ACTION_CREATE_TEAMS) {
            mTeamsViewModel.createTeams()
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