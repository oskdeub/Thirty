package model

class Dice {
    var value: Int = 0
    var isSelected : Boolean = false

    fun reset() {
        value = 0
        isSelected = false
    }

    fun roll(){
        value = (1..6).random()
    }
}
