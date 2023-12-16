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

    private var _binding: FragmentTeamsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mTeamsViewModel: TeamsViewModel
    private var mRecyclerViewData = mutableListOf<Team>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTeamsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMenu()
        setupViewModel()
        setupListeners()
        return root
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

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
                println("refresh them?")
            }
        }
    }

    private fun setupListeners() {
        val customAdapter = TeamsAdapter(mRecyclerViewData) {team ->
            val mainViewModel : MainViewModel by activityViewModels()
            mainViewModel.editingTeamId = team.id
            Navigation.findNavController(binding.root).navigate(R.id.nav_team_details)
        }

        binding.rvTeams.apply {
            adapter = customAdapter
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //TODO: Add string resource
        menu.add("Test").apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            iconTintList = ColorStateList.valueOf(Color.WHITE)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}