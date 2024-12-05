package controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import model.Combination
import se.umu.cs.seod0005.thirty.R

/**
 * Used in displaying the user-chosen combinations in a ListView.
 */
class CombinationAdapter(
    private val context: Context,
    private val combinations: MutableList<Combination>
) : BaseAdapter() {
    //Referens: Delvis autogenererad med BaseAdapter och Gemini. Metoder från BaseAdapter har jag inte manipulerat. Endast getView har jag ändrat i.
    override fun getCount(): Int {
        return combinations.size
    }

    override fun getItem(position: Int): Any {
        return combinations[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.combination_item, parent, false)

        val combination = combinations[position]
        val combinationTextView = view.findViewById<TextView>(R.id.combinationTextView)
        combinationTextView.text = combination.getCombinationString()
        val combinationItemScoreText = view.findViewById<TextView>(R.id.combinationItemScoreText)
        combinationItemScoreText.text = combination.score.toString()

        return view
    }
}