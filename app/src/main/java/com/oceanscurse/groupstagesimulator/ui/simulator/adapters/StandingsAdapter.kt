package com.oceanscurse.groupstagesimulator.ui.simulator.adapters

/**
 * Created by Raymond de la Croix on 17-12-2023.
 */

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Result

/**
 * Created by Raymond de la Croix on 14/12/2023.
 */
class StandingsAdapter(
    private val dataSet: List<Result?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        /**
         * Identifier for the header view type
         */
        const val VIEW_TYPE_HEADER = 0
        /**
         * Identifier for the player view type
         */
        const val VIEW_TYPE_RESULT = 1
    }

    /**
     * A header ViewHolder that indicated the names of the values in the table.
     * This header does not hold any dynamic values, the texts are set in R.layout.vh_result_header.
     */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /**
     * A ViewHolder for a team's result that shows the team's name and scores.
     */
    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvPosition: TextView
        private val tvTeamName: TextView
        private val tvPlayed: TextView
        private val tvWin: TextView
        private val tvDraw: TextView
        private val tvLoss: TextView
        private val tvFor: TextView
        private val tvAgainst: TextView
        private val tvNetScore: TextView
        private val tvPoints: TextView
        private val llBackground: View

        init {
            tvPosition = view.findViewById(R.id.tv_position)
            tvTeamName = view.findViewById(R.id.tv_team_name)
            tvPlayed = view.findViewById(R.id.tv_played)
            tvWin = view.findViewById(R.id.tv_win)
            tvDraw = view.findViewById(R.id.tv_draw)
            tvLoss = view.findViewById(R.id.tv_loss)
            tvFor = view.findViewById(R.id.tv_for)
            tvAgainst = view.findViewById(R.id.tv_against)
            tvNetScore = view.findViewById(R.id.tv_net_score)
            tvPoints = view.findViewById(R.id.tv_points)
            llBackground = view.findViewById(R.id.ll_background)
        }

        @SuppressLint("SetTextI18n")
        fun bind(result: Result) {
            tvPosition.text = "${result.position}."
            tvTeamName.text = "${result.team.name} (${result.team.totalTeamPoints()})"
            tvPlayed.text = result.timesPlayed.toString()
            tvWin.text = result.wins.toString()
            tvDraw.text = result.draws.toString()
            tvLoss.text = result.losses.toString()
            tvFor.text = result.goals.toString()
            tvAgainst.text = result.goalsAgainst.toString()
            tvNetScore.text = result.netScore.toString()
            tvPoints.text = result.points.toString()

            llBackground.setBackgroundColor(
                llBackground.context.getColor(
                    if (result.position < 3 && result.timesPlayed > 0) R.color.winner_green
                    else R.color.white
                )
            )
        }
    }

    /**
     * Gets the view type depending on the position on the item. The first row will always be the
     * header, otherwise a player row.
     */
    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_HEADER
        return VIEW_TYPE_RESULT
    }

    /**
     * Create new views (invoked by the layout manager). Depending on the view type will create
     * a header row view or a result row view.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_result_header, viewGroup, false))
            VIEW_TYPE_RESULT -> ResultViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_result, viewGroup, false))
            else -> {
                ResultViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_result, viewGroup, false))
            }
        }
    }

    /**
     * Triggers the viewHolder to bind the data, if there is any. Since there is no data for the
     * header view, it will be null.
     */
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ResultViewHolder) {
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












