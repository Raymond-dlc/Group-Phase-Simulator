package com.oceanscurse.groupstagesimulator.ui.teams.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        /**
         * Identifier for the header view type
         */
        const val VIEW_TYPE_HEADER = 0
        /**
         * Identifier for the player view type
         */
        const val VIEW_TYPE_PLAYER = 1
    }

    /**
     * A header ViewHolder that indicated the names of the values in the table.
     * This header does not hold any dynamic values, the texts are set in R.layout.vh_player_header.
     */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /**
     * A ViewHolder for a player that shows their name and values.
     */
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
        }

        /**
         * Sets the player's name and individual values.
         */
        fun bind(player: Player) {
            tvPlayerName.text = player.name
            tvPlayerStrength.text = "${player.strength}"
            tvPlayerSpeed.text = "${player.speed}"
            tvPlayerDefence.text = "${player.defence}"
        }
    }

    /**
     * Gets the view type depending on the position on the item. The first row will always be the
     * header, otherwise a player row.
     */
    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_HEADER
        return VIEW_TYPE_PLAYER
    }

    /**
     * Create new views (invoked by the layout manager). Depending on the view type will create
     * a header row view or a player row view.
     */
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

    /**
     * Triggers the viewHolder to bind the data, if there is any. Since there is no data for the
     * header view, it will be null.
     */
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is PlayerViewHolder) {
            dataSet[position]?.let {
                viewHolder.bind(it)
            }
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager).
     */
    override fun getItemCount() = dataSet.size

}