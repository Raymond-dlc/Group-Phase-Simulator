package com.oceanscurse.groupstagesimulator.ui.simulator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.MainActivity
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.databinding.FragmentSimulatorBinding
import com.oceanscurse.groupstagesimulator.model.Result
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.model.hasTeamsAssigned
import com.oceanscurse.groupstagesimulator.ui.simulator.adapters.RoundsAdapter
import com.oceanscurse.groupstagesimulator.ui.simulator.adapters.StandingsAdapter
import kotlinx.coroutines.launch

class SimulatorFragment : Fragment(), MenuProvider {

    companion object {
        const val ACTION_CLEAR = 0
    }

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!
    private val mSimulatorViewModel: SimulatorViewModel by viewModels()
    private val mRecyclerViewRounds = mutableListOf<Round>()
    private val mRecyclerViewResults = mutableListOf<Result?>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMenu()
        setupViewModel()
        setupEmptyState()
        setupRounds()
        setupStandings()

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
     * On the each resume the viewModel is notified to check the conditions to make sure
     * the UI will be updated if it's prepared.
     *
     * On each collection of the UiState the UI will:
     * - Update the visibility of the content and empty state, depending on if the requirements are met.
     * - Update the enabled state of the control buttons.
     * - Update the recyclerViewData with the rounds.
     * - Update the recyclerViewData with the standings.
     */
    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mSimulatorViewModel.uiState.collect { uiState ->
                    binding.llEmptyState.visibility = if (uiState.meetsRequirements) View.GONE else View.VISIBLE
                    binding.svContent.visibility = if (uiState.meetsRequirements) View.VISIBLE else View.GONE

                    uiState.groupStage?.let {
                        binding.btnAssignTeams.isEnabled = it.rounds.any { round -> !round.hasTeamsAssigned() } || it.rounds.none { round -> round.isPlayed }
                        binding.btnPlayAll.isEnabled = it.rounds.all { round -> !round.isPlayed } && it.rounds.all { round -> round.hasTeamsAssigned() }

                        mRecyclerViewRounds.clear()
                        mRecyclerViewRounds.addAll(uiState.groupStage.rounds)
                        binding.rvRounds.adapter?.notifyDataSetChanged()

                        mRecyclerViewResults.clear()
                        mRecyclerViewResults.add(null) // header
                        mRecyclerViewResults.addAll(uiState.groupStage.results)

                        binding.rvStandings.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mSimulatorViewModel.checkConditions()
            }
        }
    }

    /**
     * Sets up the OnClickListener for the empty state button. Once clicked
     * will open the drawer menu to hint the user what to do.
     */
    private fun setupEmptyState() {
        binding.btnGoToTeams.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).openDrawer()
            }
        }
    }

    /**
     * Sets up the RecyclerView and adapter for the rounds.
     * Depending on the number of Rounds that will be played, as defined in
     * Constants.NUM_ROUNDS, the layout manager will either be a GridLayoutManager
     * or a LinearLayoutManager, to nicely center the rounds when there is only a few,
     * or line them up horizontally, to make it scrollable.
     *
     * Adds a OnClickListener to the Rounds adapter, to notify the ViewModel that
     * a round needs to be played.
     *
     * Adds two OnClickListeners to more easily control the simulation.
     * - One button to assign the teams
     *      => Will trigger the viewModel to assign teams.
     * - One button play the entire GroupStage, rather than just round by round
     *      => Will trigger the viewModel to play the entire GroupStage.
     */
    private fun setupRounds() {
        val roundsAdapter = RoundsAdapter(mRecyclerViewRounds) {
            mSimulatorViewModel.playRound(it)
        }

        binding.rvRounds.apply {
            adapter = roundsAdapter
            layoutManager =
                if (Constants.NUM_ROUNDS < 4) {
                    // If we have less than 3 rounds, we use a grid layout to more nicely display them
                    // otherwise the user will be able to scroll through them horizontally.
                    layoutParams = layoutParams.apply { width = LinearLayout.LayoutParams.WRAP_CONTENT }
                    GridLayoutManager(context, 3)
                } else {
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                }
        }

        binding.btnAssignTeams.setOnClickListener {
            mSimulatorViewModel.assignTeams()
        }

        binding.btnPlayAll.setOnClickListener {
            mSimulatorViewModel.playEntireGroupStage()
        }
    }

    /**
     * Sets up the RecyclerView and adapter for the standings.
     */
    private fun setupStandings() {
        val standingsAdapter = StandingsAdapter(mRecyclerViewResults)
        binding.rvStandings.apply {
            adapter = standingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Creates the menu. The simulator fragment only contains one menu item, that will allow the
     * user to clear the current GroupStage
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, ACTION_CLEAR, 0, getString(R.string.teams_action_add_team)).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear)
        }
    }

    /**
     * Handles action bar items.
     * ACTION_CLEAR -> Will trigger the viewModel to reset the GroupStage.
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            ACTION_CLEAR -> mSimulatorViewModel.reset()
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