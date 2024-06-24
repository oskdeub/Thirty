package model

/**
 * value : Dice value
 * isSelected : Marks the Dice as selected.
 * inCombination : Marks the Dice as part of a combination.
 * inCurrentCombination : Marks the Dice as in currentCombination (used in Game class).
 */
class Dice {
    var value: Int = 0
    var isSelected : Boolean = false
    var inCombination : Boolean = false
    var inCurrentCombination : Boolean = false

    /**
     * Resets the Dice to its initial state.
     */
    fun reset() {
        value = 0
        isSelected = false
        inCombination = false
        inCurrentCombination = false
    }

    /**
     * Rolls the Dice.
     */
    fun roll(){
        value = (1..6).random()
    }
}
