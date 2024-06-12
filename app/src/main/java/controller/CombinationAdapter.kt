package controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import model.Combination
import se.umu.cs.seod0005.thirty.R

class CombinationAdapter(
    private val context: Context,
    private val combinations: MutableList<Combination>
) : BaseAdapter() {

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
        // R.layout.combination_item is your layout for a single combination item

        val combination = combinations[position]

        // Populate the views in your combination_item layout with data from 'combination'
        // Example:
        val combinationTextView = view.findViewById<TextView>(R.id.combinationTextView)
        combinationTextView.text = combination.combination.toString()

        return view
    }
}