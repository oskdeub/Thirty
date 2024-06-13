package controller

import android.content.Context
import android.widget.ArrayAdapter

class CombinationSpinnerAdapter(context: Context, resource: Int, private val items: MutableList<String>) :
    ArrayAdapter<String>(context, resource, items) {

    // You can override other methods here if needed, like getView() or getDropDownView()
}