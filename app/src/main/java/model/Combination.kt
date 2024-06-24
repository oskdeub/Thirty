package model


/**
 * Combination class stores a combination of dice. The dice values of a combination are stored in combination. The dice indecies of a combination are stored in diceIndecies.
 * If the score is at target (selected in the combination Spinner) scoreAtTarget is true (must invoke calculateScoreForTarget().
 */
class Combination {
    var combination : MutableList<Int> = mutableListOf()
    var diceIndecies : MutableList<Int> = mutableListOf()
    var score : Int = 0
    var scoreAtTarget : Boolean = false

    /**
     * Adds number to combination, and diceIndex to diceIndecies. Updates the score of the combinaiton.
     */
    fun addToCombinationAndScore(number: Int, diceIndex: Int) {
        combination.add(number)
        diceIndecies.add(diceIndex)
        score += number
    }

    /**
     * Calculates the score to see if it matches target. Sets scoreAtTarget to true if it does.
     */
    fun calculateScoreForTarget(target: Int) : Int {
        if (target == 3){ // Combination selected is LOW
            if(combination.sum() <= 3){
                scoreAtTarget = true
                return combination.sum()
            } else {
                scoreAtTarget = false
                return 0
            }
        }
        if (combination.sum() == target){
            scoreAtTarget = true
            return combination.sum()
        } else {
            scoreAtTarget = false
            return 0
        }
    }

    /**
     * Removes a number from combination and diceIndecies. Updates the score of the combinaiton.
     */
    fun removeFromCombinationAndScore(value: Int, index: Int) {
        combination.remove(value)
        diceIndecies.remove(index)
        score -= value
    }
}

