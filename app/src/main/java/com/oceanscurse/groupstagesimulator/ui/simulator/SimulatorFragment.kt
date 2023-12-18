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
import androidx.lifecycle.ViewModelProvider
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
import com.oceanscurse.groupstagesimulator.ui.teams.TeamDetailsFragment
import com.oceanscurse.groupstagesimulator.ui.teams.TeamsViewModel
import com.oceanscurse.groupstagesimulator.ui.teams.adapters.PlayersAdapter
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
        initEmptyState()
        setupRounds()
        setupStandings()

        return root
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

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

    private fun initEmptyState() {
        binding.btnGoToTeams.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).openDrawer()
            }
        }
    }

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

    private fun setupStandings() {
        val standingsAdapter = StandingsAdapter(mRecyclerViewResults)
        binding.rvStandings.apply {
            adapter = standingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, ACTION_CLEAR, 0, getString(R.string.teams_action_add_team)).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            ACTION_CLEAR -> mSimulatorViewModel.reset()
        }

        return false
    }
}