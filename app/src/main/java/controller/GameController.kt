package controller

import android.util.Log
import model.Combination
import model.Game
import model.Dice

class GameController(private val gameView: GameView, private val gameState: Game) {


    /****************************************
     **             Round Logic            **
     ****************************************/

    fun endRound() {
        addScoreToCurrentGame(gameState.currentRoundScore)
        addRoundScoreToTotalScore()
        updateScoreDisplay()
        removeUsedCombination()
        if(gameState.currentRound == 10){
            gameView.endGame(gameState.score)
        }
        resetRound()
        startRound()
    }

    fun restoreGame(state : Game){
        Log.d("RestoreGame", "${state.currentRound}, ${state.currentRoundScore}, ${state.remainingThrows}, ${state.combinationMode},")
        gameState.load(state)
        updateCurrentRoundScore()
        updateThrowsDisplay()
        updateCombinationScoreDisplay()
        updateScoreDisplay()
        updateRoundNumberDisplay()
        updateCombinationsList()
        Log.d("gameState", "${state.remainingThrows}, ${state.combinationMode}")
        setCombinationStringList()
        for(dice in gameState.diceList){
            updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
        if(gameState.remainingThrows == 0){
            setCombinationStageDisplay()
        }
    }

    private fun addRoundScoreToTotalScore() {
        gameState.score.totalScore += gameState.currentRoundScore
    }

    private fun resetThrows() {
        gameState.remainingThrows = 3
    }

    private fun resetRound() {
        gameState.currentRoundScore = 0
        resetDice()
        resetThrows()
        setResetRoundDisplays()
        gameState.currentCombination = Combination()
    }

    private fun startRound() {
        gameState.currentRound += 1
        updateRoundNumberDisplay()
        resetDice()
    }

    fun startGame() {
        startRound()
    }
    /****************************************
     **             Dice Logic             **
     ****************************************/
    fun handleThrowButtonClick() {
        if (gameState.remainingThrows > 0) {
            rollDice()
            gameState.remainingThrows -= 1
            updateThrowsDisplay()
        }
        if (gameState.remainingThrows == 0) {
            setCombinationStageDisplay()
        }
    }
    private fun rollDice() {
        for (dice in gameState.diceList) {
            if (!dice.isSelected && !dice.inCombination) {
                dice.roll()
                Log.d("DiceRoll", "Dice${gameState.diceList.indexOf(dice) + 1} rolled: ${dice.value}")
                gameView.updateDiceImage(
                    gameState.diceList.indexOf(dice), dice.value, false, dice.inCombination
                )
            }
        }
    }
    private fun resetDice() {
        for (dice in gameState.diceList) {
            dice.reset()
            updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
    }

    private fun handleDiceSaveClick(index: Int) {
        if (gameState.remainingThrows < 3) {
            toggleDiceSelection(gameState.diceList[index])
        }
    }

    private fun toggleDiceSelection(dice: Dice) {
        dice.isSelected = !dice.isSelected
        updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    fun handleDiceClick(index: Int) {
        if (gameState.combinationMode) {
            handleDiceCombinationClick(index)
            Log.d(
                "Combination",
                "Dice${index + 1} added to currentCombination: ${gameState.currentCombination.combination}"
            )
            return
        } else {
            handleDiceSaveClick(index)
            Log.d("Dice", "Dice${index + 1} saved: ${gameState.diceList[index].value}")
        }
    }
    private fun resetDiceStatus(dice: Dice) {
        dice.inCombination = false
        updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    private fun updateCurrentRoundScore() {
        gameState.currentRoundScore = 0
        for (combination in gameState.combinationList) {
            gameState.currentRoundScore += combination.calculateScoreForTarget(gameState.targetScore)
        }
        updateCombinationScoreDisplay()
    }

    /****************************************
     **       Combination Logic            **
     ****************************************/

    fun handleCombinationButtonClick() {
        toggleCombinationMode()
        if (gameState.combinationMode) {
            startNewCombination()
        } else {
            addCombinationToCombinationList(gameState.currentCombination)
        }
    }
    private fun handleDiceCombinationClick(index: Int) {
        val dice = gameState.diceList[index]
        if (!dice.inCombination) {
            gameState.currentCombination.addToCombinationAndScore(dice.value, index)
            Log.d(
                "Combination",
                "Dice${index + 1} added to currentCombination: ${dice.value}, Indecies: ${gameState.currentCombination.diceIndecies}"
            )
            dice.inCombination = true
            updateDiceImage(index, dice.value, dice.isSelected, dice.inCombination)
        } else {
            gameState.currentCombination.removeFromCombinationAndScore(dice.value, index)
            resetDiceStatus(dice)
        }
    }

    private fun startNewCombination() {
        gameState.currentCombination = Combination()
    }

    private fun toggleCombinationMode() {
        gameState.combinationMode = !gameState.combinationMode
        toggleCombinationDisplay()
    }

    private fun addCombinationToCombinationList(combination: Combination) {
        if (combination.score > 0) {
            gameState.combinationList.add(combination)
            gameState.currentRoundScore += combination.calculateScoreForTarget(gameState.targetScore)
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
        if (gameState.targetScore == 3) {
            removeCombinationFromSpinner("Low")
        } else {
            removeCombinationFromSpinner(gameState.targetScore.toString())
        }
    }
    private fun removeCombinationFromCombinationList(combination: Combination) {
        gameState.combinationList.remove(combination)
        updateCurrentRoundScore()
        updateCombinationsList()
        for (diceIndex in combination.diceIndecies) {
            resetDiceStatus(gameState.diceList[diceIndex])
        }
    }

    fun handleCombinationSelect(selectedCombination: String?) {
        gameState.targetScore = when (selectedCombination) {
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
        when (gameState.targetScore) {
            3 -> {
                gameState.score.low = currentRoundScore
            }

            4 -> {
                gameState.score.four = currentRoundScore
            }

            5 -> {
                gameState.score.five = currentRoundScore
            }

            6 -> {
                gameState.score.six = currentRoundScore
            }

            7 -> {
                gameState.score.seven = currentRoundScore
            }

            8 -> {
                gameState.score.eight = currentRoundScore
            }

            9 -> {
                gameState.score.nine = currentRoundScore
            }

            10 -> {
                gameState.score.ten = currentRoundScore
            }

            11 -> {
                gameState.score.eleven = currentRoundScore
            }

            12 -> {
                gameState.score.twelve = currentRoundScore
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
        gameView.updateThrowsDisplay(gameState.remainingThrows)
        gameView.updateCombinationScoreDisplay(0)
        gameView.updateMarkCombinationButtonEnabled(false)
        gameView.updateThrowButtonEnabled(true)
        gameView.updateEndRoundButtonEnabled(false)
        gameState.combinationList.clear()
        gameView.updateCombinationsList(gameState.combinationList)
    }
    private fun updateCombinationsList() {
        gameView.updateCombinationsList(gameState.combinationList)
    }

    private fun updateThrowsDisplay() {
        gameView.updateThrowsDisplay(gameState.remainingThrows)
    }

    private fun setCombinationStageDisplay() {
        gameView.updateMarkCombinationButtonEnabled(true)
        gameView.updateThrowButtonEnabled(false)
        gameView.updateEndRoundButtonEnabled(true)
    }

    private fun updateCombinationScoreDisplay() {
        gameView.updateCombinationScoreDisplay(gameState.currentRoundScore)
    }

    private fun toggleCombinationDisplay() {
        gameView.updateMarkCombinationDisplay(gameState.combinationMode)
    }

    private fun removeCombinationFromSpinner(combination: String) {
        gameState.combinationStringList.remove(combination)
        gameView.removeCombinationFromSpinner(combination)
    }

    private fun setCombinationStringList() {
        gameView.setCombinationSpinnerItems(gameState.combinationStringList)
    }

    private fun updateScoreDisplay() {
        gameView.updateScoreDisplay(gameState.score.totalScore)
    }

    private fun updateRoundNumberDisplay() {
        gameView.updateRoundNumberDisplay(gameState.currentRound)
    }
}