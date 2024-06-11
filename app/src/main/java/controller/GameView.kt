package controller

// In a separate file, e.g., GameView.kt
interface GameView {
    fun updateDiceImage(diceIndex: Int, diceValue: Int, color: Int)
    fun updateScoreDisplay(newScore: Int)
    // Add other methods for UI updates as needed
}