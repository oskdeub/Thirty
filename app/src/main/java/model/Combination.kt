package model

class Combination {
    var combination : MutableList<Int> = mutableListOf()
    var diceIndecies : MutableList<Int> = mutableListOf()
    var score : Int = 0
    var scoreAtTarget : Boolean = false

    fun addToCombinationAndScore(number: Int, diceIndex: Int) {
        combination.add(number)
        diceIndecies.add(diceIndex)
        score += number
    }
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
}

