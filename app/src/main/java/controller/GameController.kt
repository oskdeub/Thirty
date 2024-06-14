package controller

import android.util.Log
import model.Combination
import model.Game
import model.Dice

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
    private var targetScore: Int = 0
    private var currentRoundScore: Int = 0

    private fun resetDice() {
        for (dice in diceList) {
            dice.reset()
            updateDiceImage(diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
    }

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

    private fun rollDice() {
        for (dice in diceList) {
            if (!dice.isSelected && !dice.inCombination) {
                dice.roll()
                Log.d("Diceroll", "Dice${diceList.indexOf(dice) + 1} rolled: ${dice.value}")
                gameView.updateDiceImage(
                    diceList.indexOf(dice), dice.value, false, dice.inCombination
                )
            }
        }
    }

    private fun updateThrowsDisplay() {
        gameView.updateThrowsDisplay(currentGame.remainingThrows)
    }

    private fun setCombinationStageDisplay() {
        gameView.updateMarkCombinationButtonEnabled(true)
        gameView.updateThrowButtonEnabled(false)
        gameView.updateEndRoundButtonEnabled(true)
    }

    fun endRound() {
        addScoreToCurrentGame(currentRoundScore)
        addRoundScoreToTotalScore()
        updateScoreDisplay()
        removeUsedCombination()
        resetRound()
        startRound()
    }

    private fun removeUsedCombination() {
        if (targetScore == 3) {
            gameView.removeCombinationFromSpinner("Low")
        } else {
            gameView.removeCombinationFromSpinner(targetScore.toString())
        }
    }

    private fun updateScoreDisplay() {
        gameView.updateScoreDisplay(currentGame.score.totalScore)
    }

    private fun addRoundScoreToTotalScore() {
        currentGame.score.totalScore += currentRoundScore
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

    private fun setResetRoundDisplays() {
        gameView.updateThrowsDisplay(currentGame.remainingThrows)
        gameView.updateCombinationScoreDisplay(0)
        gameView.updateMarkCombinationButtonEnabled(false)
        gameView.updateThrowButtonEnabled(true)
        gameView.updateEndRoundButtonEnabled(false)
        combinationList.clear()
        gameView.updateCombinationsList(combinationList)
    }

    private fun startRound() {
        currentGame.currentRound += 1
        gameView.updateRoundNumberDisplay(currentGame.currentRound)
        resetDice()
    }

    fun startGame() {
        startRound()

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
            updateDiceImage(index, dice.value, true, true)
        }
    }

    private fun updateDiceImage(index: Int, value: Int, isSelected: Boolean, inCombination: Boolean
    ) {
        gameView.updateDiceImage(index, value, isSelected, inCombination)
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

    private fun updateCurrentRoundScore() {
        currentRoundScore = 0
        for (combination in combinationList) {
            currentRoundScore += combination.calculateScoreForTarget(targetScore)
        }
        updateCombinationScoreDisplay(currentRoundScore)
    }

    private fun updateCombinationScoreDisplay(score: Int) {
        gameView.updateCombinationScoreDisplay(score)
    }

    private fun toggleCombinationDisplay() {
        gameView.updateMarkCombinationDisplay(combinationMode)
    }

    fun handleCombinationButtonClick() {
        toggleCombinationMode()
        if (combinationMode) {
            startNewCombination()
        } else {
            addCombinationToCombinationList(currentCombination)
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
            updateCombinationScoreDisplay(currentRoundScore)
            gameView.updateCombinationsList(combinationList)
        } //Else throw away combination?
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

    fun handleRemoveCombinationClick(clickedCombination: Combination) {
        removeCombinationFromCombinationList(clickedCombination)
        updateCombinationsList()
        Log.d("Combination", "Combination removed: $clickedCombination")
    }

    private fun removeCombinationFromCombinationList(combination: Combination) {
        combinationList.remove(combination)
        updateCurrentRoundScore()
        updateCombinationsList()
        for (diceIndex in combination.diceIndecies) {
            resetDiceStatus(diceList[diceIndex])
        }
    }

    private fun resetDiceStatus(dice: Dice) {
        dice.inCombination = false
        dice.isSelected = false
        updateDiceImage(diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    private fun updateCombinationsList() {
        gameView.updateCombinationsList(combinationList)
    }
}