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
    fun getDiceState(): BooleanArray {
        return arrayOf(isSelected, inCombination, inCurrentCombination).toBooleanArray();
    }
    /**
     * Rolls the Dice.
     */
    fun roll(){
        value = (1..6).random()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "value" to value,
            "isSelected" to isSelected,
            "inCombination" to inCombination,
            "inCurrentCombination" to inCurrentCombination
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Dice {
            val dice = Dice()
            dice.value = map["value"] as Int
            dice.isSelected = map["isSelected"] as Boolean
            dice.inCombination = map["inCombination"] as Boolean
            dice.inCurrentCombination = map["inCurrentCombination"] as Boolean
            return dice
        }
    }
}
