package com.oceanscurse.groupstagesimulator.ui.teams.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Team

/**
 * Created by Raymond de la Croix on 14/12/2023.
 */
class TeamsAdapter(
    private val dataSet: List<Team>,
    private val onItemClicked: (Team) -> Unit
) : RecyclerView.Adapter<TeamsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(view: View, onItemClicked: (Team) -> Unit) : RecyclerView.ViewHolder(view) {
        private val tvTeamName: TextView
        private val ivTeamImage: ImageView

        private var currentTeam: Team? = null

        init {
            tvTeamName = view.findViewById(R.id.tv_team_name)
            ivTeamImage = view.findViewById(R.id.iv_team_image)
            view.setOnClickListener {
                currentTeam?.let {
                    onItemClicked(it)
                }
            }
        }

        fun bind(team: Team) {
            currentTeam = team
            tvTeamName.text = if (!team.isComplete()) tvTeamName.context.getString(R.string.teams_add_team) else team.name
            ivTeamImage.setImageResource(team.logoResourceId)
            ivTeamImage.alpha = if (!team.isComplete()) 0.2f else 1.0f
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_team, viewGroup, false)
        return ViewHolder(view, onItemClicked)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}