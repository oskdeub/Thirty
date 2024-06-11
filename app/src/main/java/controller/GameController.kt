package controller

import android.util.Log
import model.Game
import model.Dice
import model.Score
import view.MainActivity
class GameController(private val gameView: GameView) {
    private var currentGame: Game? = null
    private val diceList: List<Dice> = listOf(
        Dice(),
        Dice(),
        Dice(),
        Dice(),
        Dice(),
        Dice(),
    )
    fun resetDice(){
        for (dice in diceList) {
            dice.reset()
        }
    }

    fun rollDice() {
        for (dice in diceList){
            if (!dice.isSelected){
                dice.roll()
                Log.d("Diceroll", "Dice${diceList.indexOf(dice) + 1} rolled: ${dice.value}")
                gameView.updateDiceImage(diceList.indexOf(dice), dice.value, 1)
            }
        }
    }
    fun startGame() {
        // TODO: Implement logic to start a new game
        currentGame = Game()
        resetDice()

    }
    fun handleCombinationSelected(selectedCombination: String?) {
        // TODO: Implement logic to calc score and update game state
    }

    fun handleDiceClick(i: Int) {
        val clickedDice = diceList[i]
        clickedDice.isSelected = !clickedDice.isSelected

        Log.d("Dice", "Dice${i + 1} rolled: ${clickedDice.value}")

    }
}