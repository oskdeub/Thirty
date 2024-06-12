package controller

import android.util.Log
import model.Combination
import model.Game
import model.Dice
import model.Score
import view.MainActivity
class GameController(private val gameView: GameView) {
    private var currentGame: Game = Game()
    private val diceList: List<Dice> = listOf(
        Dice(),
        Dice(),
        Dice(),
        Dice(),
        Dice(),
        Dice(),
    )
    private var combinationMode: Boolean = false
    private var combinationList: MutableList<Combination> = mutableListOf()
    private var currentCombination: Combination = Combination()
    private fun resetDice(){
        for (dice in diceList) {
            dice.reset()
        }
    }

   fun rollDice() {
        if (currentGame.remainingThrows > 0) {
            for (dice in diceList) {
                if (!dice.isSelected && !dice.inCombination) {
                    dice.roll()
                    Log.d("Diceroll", "Dice${diceList.indexOf(dice) + 1} rolled: ${dice.value}")
                    gameView.updateDiceImage(diceList.indexOf(dice), dice.value, false, dice.inCombination)
                }
            }
            currentGame.remainingThrows -= 1
            gameView.updateThrowsDisplay(currentGame.remainingThrows)
        }
        if (currentGame.remainingThrows == 0) {
            endRound()
        }
    }

    private fun endRound() {
        gameView.updateMarkCombinationButtonEnabled(true)
        gameView.updateThrowButtonEnabled(false)
    }

    private fun resetThrows() {
        currentGame.remainingThrows = 3
    }
    fun startGame() {
        resetDice()

    }
    private fun handleDiceCombinationClick(i: Int) {
        val dice = diceList[i]
        if(!dice.inCombination) {
            currentCombination.combination.add(dice.value)
            dice.inCombination = true
            gameView.updateDiceImage(i, dice.value, true, true)
        }
    }

    private fun handleDiceSaveClick(i: Int) {
        if (currentGame.remainingThrows < 3) {
            val clickedDice = diceList[i]
            clickedDice.isSelected = !clickedDice.isSelected
            gameView.updateDiceImage(i, clickedDice.value, clickedDice.isSelected, clickedDice.inCombination)

        }
    }

    fun handleDiceClick(i: Int) {
        if (combinationMode) {
            handleDiceCombinationClick(i)
            Log.d("Combination", "Dice${i + 1} added to currentCombination: ${currentCombination.combination}")
            return
        } else {
            handleDiceSaveClick(i)
            Log.d("Dice", "Dice${i + 1} saved: ${diceList[i].value}")
        }
    }

    fun combinationMode() {
        combinationMode = !combinationMode
        gameView.updateMarkCombinationDisplay(combinationMode)
        if(combinationMode){
            currentCombination = Combination()
        } else {
            combinationList.add(currentCombination)
            gameView.updateCombinationsList(combinationList)
        }
    }

    fun handleCombinationSelect(selectedCombination: String?) {

    }
}