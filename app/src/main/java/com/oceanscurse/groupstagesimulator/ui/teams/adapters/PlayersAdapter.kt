package com.oceanscurse.groupstagesimulator.ui.teams.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Player

/**
 * Created by Raymond de la Croix on 14/12/2023.
 */
class PlayersAdapter(
    private val dataSet: List<Player?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_PLAYER = 1
        val xPositions = mutableListOf<Float>()
    }

    // A header view holder that indicated the names of the values in the table.
    // This header does not hold any dynamic values, the texts are set in R.layout.vh_player_header.
    // To make sure the values are aligned properly with the headers, independent of language or
    // screen size, the x values are cached and set to the textViews. Number of children have to match.
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.post {
                xPositions.addAll((view as ViewGroup).children.map { it.x })
            }
        }
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvPlayerName: TextView
        private val tvPlayerStrength: TextView
        private val tvPlayerSpeed: TextView
        private val tvPlayerDefence: TextView

        init {
            tvPlayerName = view.findViewById(R.id.tv_player_name)
            tvPlayerStrength = view.findViewById(R.id.tv_player_strength)
            tvPlayerSpeed = view.findViewById(R.id.tv_player_speed)
            tvPlayerDefence = view.findViewById(R.id.tv_player_defence)
            tvPlayerName.visibility = View.GONE
            tvPlayerStrength.visibility = View.GONE
            tvPlayerSpeed.visibility = View.GONE
            tvPlayerDefence.visibility = View.GONE

            view.post {
                (view as ViewGroup).children.forEachIndexed { index, valueView ->
                    valueView.x = xPositions[index]
                    valueView.visibility = View.VISIBLE
                }
            }
        }

        fun bind(player: Player) {
            tvPlayerName.text = player.name
            tvPlayerStrength.text = "${player.strength}"
            tvPlayerSpeed.text = "${player.speed}"
            tvPlayerDefence.text = "${player.defence}"
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_HEADER;
        return VIEW_TYPE_PLAYER;
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new view, which defines the UI of the list item

        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_player_header, viewGroup, false))
            VIEW_TYPE_PLAYER -> PlayerViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_player, viewGroup, false))
            else -> {
                PlayerViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_player, viewGroup, false))
            }
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is PlayerViewHolder) {
            dataSet[position]?.let {
                viewHolder.bind(it)
            }
        }
    }

    override fun getItemCount() = dataSet.size

}