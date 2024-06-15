package controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import se.umu.cs.seod0005.thirty.R

class ScoreListAdapter(context: Context, private val labelArray: List<String>, private val scoreArray: List<Int>) : ArrayAdapter<String>(context, 0, labelArray) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.score_item_layout, parent, false)

        // Access labels and values using the position
        val label = labelArray[position]
        val score = scoreArray[position]

        val scoreLabelTextView = itemView.findViewById<TextView>(R.id.scoreLabelTextView)
        val scoreValueTextView = itemView.findViewById<TextView>(R.id.scoreValueTextView)
        scoreLabelTextView.text = label
        scoreValueTextView.text = score.toString()

        return itemView
    }
}