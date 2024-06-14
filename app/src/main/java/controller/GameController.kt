package controller

import android.util.Log
import model.Combination
import model.Game
import model.Dice

class GameController(private val gameView: GameView) {
    private var currentGame: Game = Game()
    private val diceList: List<Dice> = listOf(
        Dice(), //Dice1
        Dice(), //Dice2
        Dice(), //Dice3
        Dice(), //Dice4
        Dice(), //Dice5
        Dice(), //Dice6
    )
    private var combinationMode: Boolean = false
    private var combinationList: MutableList<Combination> = mutableListOf()
    private var currentCombination: Combination = Combination()
    private var targetScore: Int = 0
    private var currentRoundScore: Int = 0

    fun handleThrowButtonClick() {
        if (currentGame.remainingThrows > 0) {
            rollDice()
            currentGame.remainingThrows -= 1
            updateThrowsDisplay()
        }
        if (currentGame.remainingThrows == 0) {
            setCombinationStageDisplay()
        }
    }

    /****************************************
     **             Round Logic            **
     ****************************************/

    fun endRound() {
        addScoreToCurrentGame(currentRoundScore)
        addRoundScoreToTotalScore()
        updateScoreDisplay()
        removeUsedCombination()
        resetRound()
        startRound()
    }

    private fun addRoundScoreToTotalScore() {
        currentGame.score.totalScore += currentRoundScore
    }

    private fun resetThrows() {
        currentGame.remainingThrows = 3
    }

    private fun resetRound() {
        currentRoundScore = 0
        resetDice()
        resetThrows()
        setResetRoundDisplays()
        currentCombination = Combination()
    }

    private fun startRound() {
        currentGame.currentRound += 1
        updateRoundNumberDisplay()
        resetDice()
    }

    fun startGame() {
        startRound()
    }

    /****************************************
     **             Dice Logic             **
     ****************************************/

    private fun rollDice() {
        for (dice in diceList) {
            if (!dice.isSelected && !dice.inCombination) {
                dice.roll()
                Log.d("DiceRoll", "Dice${diceList.indexOf(dice) + 1} rolled: ${dice.value}")
                gameView.updateDiceImage(
                    diceList.indexOf(dice), dice.value, false, dice.inCombination
                )
            }
        }
    }
    private fun resetDice() {
        for (dice in diceList) {
            dice.reset()
            updateDiceImage(diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
    }

    private fun handleDiceSaveClick(index: Int) {
        if (currentGame.remainingThrows < 3) {
            toggleDiceSelection(diceList[index])
        }
    }

    private fun toggleDiceSelection(dice: Dice) {
        dice.isSelected = !dice.isSelected
        updateDiceImage(diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    fun handleDiceClick(index: Int) {
        if (combinationMode) {
            handleDiceCombinationClick(index)
            Log.d(
                "Combination",
                "Dice${index + 1} added to currentCombination: ${currentCombination.combination}"
            )
            return
        } else {
            handleDiceSaveClick(index)
            Log.d("Dice", "Dice${index + 1} saved: ${diceList[index].value}")
        }
    }
    private fun resetDiceStatus(dice: Dice) {
        dice.inCombination = false
        dice.isSelected = false
        updateDiceImage(diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    private fun updateCurrentRoundScore() {
        currentRoundScore = 0
        for (combination in combinationList) {
            currentRoundScore += combination.calculateScoreForTarget(targetScore)
        }
        updateCombinationScoreDisplay()
    }

    /****************************************
     **       Combination Logic            **
     ****************************************/

    fun handleCombinationButtonClick() {
        toggleCombinationMode()
        if (combinationMode) {
            startNewCombination()
        } else {
            addCombinationToCombinationList(currentCombination)
        }
    }
    private fun handleDiceCombinationClick(index: Int) {
        val dice = diceList[index]
        if (!dice.inCombination) {
            currentCombination.addToCombinationAndScore(dice.value, index)
            Log.d(
                "Combination",
                "Dice${index + 1} added to currentCombination: ${dice.value}, Indecies: ${currentCombination.diceIndecies}"
            )
            dice.inCombination = true
            updateDiceImage(index, dice.value, dice.isSelected, dice.inCombination)
        }
    }

    private fun startNewCombination() {
        currentCombination = Combination()
    }

    private fun toggleCombinationMode() {
        combinationMode = !combinationMode
        toggleCombinationDisplay()
    }

    private fun addCombinationToCombinationList(combination: Combination) {
        if (combination.score > 0) {
            combinationList.add(combination)
            currentRoundScore += combination.calculateScoreForTarget(targetScore)
            updateCombinationScoreDisplay()
            updateCombinationsList()
        } //Else throw away combination?
    }

    fun handleRemoveCombinationClick(clickedCombination: Combination) {
        removeCombinationFromCombinationList(clickedCombination)
        updateCombinationsList()
        Log.d("Combination", "Combination removed: $clickedCombination")
    }
    private fun removeUsedCombination() {
        if (targetScore == 3) {
            removeCombinationFromSpinner("Low")
        } else {
            removeCombinationFromSpinner(targetScore.toString())
        }
    }
    private fun removeCombinationFromCombinationList(combination: Combination) {
        combinationList.remove(combination)
        updateCurrentRoundScore()
        updateCombinationsList()
        for (diceIndex in combination.diceIndecies) {
            resetDiceStatus(diceList[diceIndex])
        }
    }

    fun handleCombinationSelect(selectedCombination: String?) {
        targetScore = when (selectedCombination) {
            "Low" -> {
                3
            }

            "4" -> {
                4
            }

            "5" -> {
                5
            }

            "6" -> {
                6
            }

            "7" -> {
                7
            }

            "8" -> {
                8
            }

            "9" -> {
                9
            }

            "10" -> {
                10
            }

            "11" -> {
                11
            }

            "12" -> {
                12
            }

            else -> {
                0
            }
        }
        updateCurrentRoundScore()
    }
    private fun addScoreToCurrentGame(currentRoundScore: Int) {
        when (targetScore) {
            3 -> {
                currentGame.score.low = currentRoundScore
            }

            4 -> {
                currentGame.score.four = currentRoundScore
            }

            5 -> {
                currentGame.score.five = currentRoundScore
            }

            6 -> {
                currentGame.score.six = currentRoundScore
            }

            7 -> {
                currentGame.score.seven = currentRoundScore
            }

            8 -> {
                currentGame.score.eight = currentRoundScore
            }

            9 -> {
                currentGame.score.nine = currentRoundScore
            }

            10 -> {
                currentGame.score.ten = currentRoundScore
            }

            11 -> {
                currentGame.score.eleven = currentRoundScore
            }

            12 -> {
                currentGame.score.twelve = currentRoundScore
            }
        }
    }

    /****************************************
     **         gameView functions         **
     ****************************************/

    private fun updateDiceImage(index: Int, value: Int, isSelected: Boolean, inCombination: Boolean) {
        gameView.updateDiceImage(index, value, isSelected, inCombination)
    }
    private fun setResetRoundDisplays() {
        gameView.updateThrowsDisplay(currentGame.remainingThrows)
        gameView.updateCombinationScoreDisplay(0)
        gameView.updateMarkCombinationButtonEnabled(false)
        gameView.updateThrowButtonEnabled(true)
        gameView.updateEndRoundButtonEnabled(false)
        combinationList.clear()
        gameView.updateCombinationsList(combinationList)
    }
    private fun updateCombinationsList() {
        gameView.updateCombinationsList(combinationList)
    }

    private fun updateThrowsDisplay() {
        gameView.updateThrowsDisplay(currentGame.remainingThrows)
    }

    private fun setCombinationStageDisplay() {
        gameView.updateMarkCombinationButtonEnabled(true)
        gameView.updateThrowButtonEnabled(false)
        gameView.updateEndRoundButtonEnabled(true)
    }

    private fun updateCombinationScoreDisplay() {
        gameView.updateCombinationScoreDisplay(currentRoundScore)
    }

    private fun toggleCombinationDisplay() {
        gameView.updateMarkCombinationDisplay(combinationMode)
    }

    private fun removeCombinationFromSpinner(combination: String) {
        gameView.removeCombinationFromSpinner(combination)
    }

    private fun updateScoreDisplay() {
        gameView.updateScoreDisplay(currentGame.score.totalScore)
    }

    private fun updateRoundNumberDisplay() {
        gameView.updateRoundNumberDisplay(currentGame.currentRound)
    }
}