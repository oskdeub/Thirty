package model

class Dice {
    var value: Int = 0
    var isSelected : Boolean = false
    var inCombination : Boolean = false
    var inCurrentCombination : Boolean = false

    fun reset() {
        value = 0
        isSelected = false
        inCombination = false
    }

    fun roll(){
        value = (1..6).random()
    }
}
