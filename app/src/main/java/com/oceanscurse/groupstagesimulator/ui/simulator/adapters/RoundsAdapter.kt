package com.oceanscurse.groupstagesimulator.ui.simulator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.utilities.toDp
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
        private var currentRound: Round? = null

        init {
            rvRoundTitle = view.findViewById(R.id.tv_round_title)
            llRoundLines = view.findViewById(R.id.ll_round_lines)
            for (i in 0 until Constants.NUM_MATCHES_PER_ROUND) {
                llRoundLines.addView(createMatchLine(llRoundLines.context))
            }
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
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.endToStart = tvScore.id
                    lp.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE
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
                    lp.startToEnd = tvHomeTeam.id
                    lp.endToStart = tvAwayTeam.id
                    lp.marginStart = 16.toPixels(resources)
                    lp.marginEnd = 16.toPixels(resources)
                    lp.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE
                }

                text = context.getString(R.string.placeholder_score)
            }
            tvAwayTeam.apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).also { lp ->
                    lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToEnd = tvScore.id
                    lp.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE
                }

                text = context.getString(R.string.placeholder_team)
            }

            return ConstraintLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8.toPixels(resources), 0, 0)
                addView(tvHomeTeam)
                addView(tvScore)
                addView(tvAwayTeam)
            }
        }

        fun bind(round: Round) {
            currentRound = round
            rvRoundTitle.text = rvRoundTitle.context.getString(R.string.simulator_round_plus_number, round.id + 1)
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