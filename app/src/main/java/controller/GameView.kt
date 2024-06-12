package controller

interface GameView {
    fun updateDiceImage(diceIndex: Int, diceValue: Int, selected: Boolean, inCombination: Boolean)
    fun updateScoreDisplay(newScore: Int)
    fun updateThrowsDisplay(throwsLeft: Int)
}