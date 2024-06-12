package controller

import model.Combination

interface GameView {
    fun updateDiceImage(diceIndex: Int, diceValue: Int, selected: Boolean, inCombination: Boolean)
    fun updateScoreDisplay(newScore: Int)
    fun updateThrowsDisplay(throwsLeft: Int)
    fun updateCombinationsList(combinations: MutableList<Combination>)
    fun updateMarkCombinationDisplay(inCombinationMode: Boolean)
    fun updateRoundScoreDisplay(roundScore: Int)
    fun updateCombinationScoreDisplay(combinationScore: Int)
    fun updateThrowButtonEnabled(enabled: Boolean)
    fun updateMarkCombinationButtonEnabled(enabled: Boolean)
}