package com.oceanscurse.groupstagesimulator.ui.simulator.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.utilities.toPixels

/**
 * Created by Raymond de la Croix on 17-12-2023.
 */
class RoundsAdapter(
    private val dataSet: List<Round>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<RoundsAdapter.ViewHolder>() {

    class ViewHolder(view: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        private val rvRoundTitle: TextView
        private val llRoundLines: LinearLayout
        private val btnPlay: Button
        private var currentRound: Round? = null

        init {
            rvRoundTitle = view.findViewById(R.id.tv_round_title)
            llRoundLines = view.findViewById(R.id.ll_round_lines)
            btnPlay = view.findViewById(R.id.btn_play)
            for (i in 0 until Constants.NUM_MATCHES_PER_ROUND) {
                llRoundLines.addView(createMatchLine(llRoundLines.context))
            }

            btnPlay.setOnClickListener {
                onItemClicked(currentRound?.id ?: -1)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(round: Round) {
            currentRound = round
            rvRoundTitle.text = rvRoundTitle.context.getString(R.string.simulator_round_plus_number, round.id + 1)
            round.matches.forEachIndexed { index, match ->
                // index is offset by 2 due to the round title and the header.
                ((llRoundLines[index + 2] as ConstraintLayout)[0] as TextView).text = match.homeTeam?.name
                ((llRoundLines[index + 2] as ConstraintLayout)[1] as TextView).text = if (round.isPlayed) {
                    "${match.homeTeamScore} - ${match.awayTeamScore}"
                } else {
                    "X - X"
                }
                ((llRoundLines[index + 2] as ConstraintLayout)[2] as TextView).text = match.awayTeam?.name
            }

            btnPlay.visibility = if (round.isPlayed) View.GONE else View.VISIBLE
        }

        /**
         * Generates a constraint layout containing 3 textViews;
         * - Child 0: Home team Text View
         * - Child 1: Score
         * - Child 2: Away team Text View
         */
        private fun createMatchLine(context: Context): ConstraintLayout {
            val tvHomeTeam = TextView(context).apply {
                id = View.generateViewId()
            }
            val tvScore = TextView(context).apply {
                id = View.generateViewId()
            }
            val tvAwayTeam = TextView(context).apply {
                id = View.generateViewId()
            }

            tvHomeTeam.apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).also { lp ->
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                }

                text = context.getString(R.string.placeholder_team)
            }
            tvScore.apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).also { lp ->
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                }

                text = context.getString(R.string.placeholder_score)
            }
            tvAwayTeam.apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).also { lp ->
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                }

                text = context.getString(R.string.placeholder_team)
            }

            return ConstraintLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8.toPixels(resources), 0, 0)
                addView(tvHomeTeam)
                addView(tvScore)
                addView(tvAwayTeam)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RoundsAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_round, viewGroup, false)
        return RoundsAdapter.ViewHolder(view, onItemClicked)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: RoundsAdapter.ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}