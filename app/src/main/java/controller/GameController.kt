package controller

import android.util.Log
import model.Combination
import model.Game
import model.Dice

class GameController(private val gameView: GameView, private val gameState: Game) {


    /****************************************
     **             Round Logic            **
     ****************************************/
    /**
     *  Handles end round logic: adds score to current game. Updates UI: 'Total Score', and removes the combination that was used.
     *  Ends the game if currentRound is the last round (10).
     */
    fun endRound() {
        addScoreToCurrentGame(gameState.currentRoundScore)
        addRoundScoreToTotalScore()
        updateScoreDisplay()
        removeUsedCombination()
        if(gameState.combinationMode){
            toggleCombinationMode()
        }

        if(gameState.currentRound == 10){
            gameView.endGame(gameState.score)
        } else {
            resetRound()
            startRound()
        }
    }

    /**
     * Restores game from a Game-class object. Updates the UI to correspond to the load.
     */
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

    /**
     * Resets round specific information and calls for UI updates. The combination inside gameState gets a fresh initialization.
     */
    private fun resetRound() {
        gameState.currentRoundScore = 0
        resetDice()
        resetThrows()
        setResetRoundDisplays()
        gameState.currentCombination = Combination()
    }

    /**
     * Increments gameState.currentRound by 1. Calls for a UI update to display the change and resets the Dice.
     */
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

    /**
     * Throw-button logic. If gameState.remainingThrows is larger than 0, a dice roll takes place, remainingThrows are decreased by 1 and a UI call is made to update remaining throws display.
     * If remaningThrows then is 0, a UI call is made to display other options for the user.
     */
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

    /**
     * Dice roll logic. If the dice to be rolled is not selected nor in a combination, the dice will roll and call for an UI update of its image.
     */
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

    /**
     * Reset all dice in diceList. Calls for an UI update of the dice.
     */
    private fun resetDice() {
        for (dice in gameState.diceList) {
            dice.reset()
            updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
    }

    /**
     * If remaining throws is below 3, i.e. the player has rolled at least the first roll; this function saves a clicked dice with toggleDiceSelection().
     */
    private fun handleDiceSaveClick(index: Int) {
        if (gameState.remainingThrows < 3) {
            toggleDiceSelection(gameState.diceList[index])
        }
    }

    /**
     * if dice is selected: unselect it. If dice is not selected: select it. Update the dice image.
     */
    private fun toggleDiceSelection(dice: Dice) {
        dice.isSelected = !dice.isSelected
        updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    /**
     * Handles a dice being clicked. If the game is in combination mode, the dice gets added to the current combination.
     * If not in combination mode, saves the dice and marks it as selected.
     */
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

    /**
     * Resets the Dice inCombination status, and calls for the update of its UI image.
     */
    private fun resetDiceStatus(dice: Dice) {
        dice.inCombination = false
        dice.inCurrentCombination = false
        updateDiceImage(gameState.diceList.indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    /**
     * Calculates the score of the combinations in combinationList. Only accepts combination scores which are at the target score.
     * Updates the UI for Combination score.
     */
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
    /**
     * Handles click event for the combination button.
     * If clicked when not in combination mode: start the combination mode (gameState.combinationMode).
     * If clicked when in combination mode: add the combination to combinationList.
     */
    fun handleCombinationButtonClick() {
        toggleCombinationMode()
        if (gameState.combinationMode) {
            startNewCombination()
        } else {
            addCombinationToCombinationList(gameState.currentCombination)
        }
    }

    /**
     * PRE: gameState.combinationMode = true
     * Handles clicking a dice while in combination mode. If Dice is not in currentCombination: Adds the dice to the current combination.
     * If Dice is in currentCombination
     */
    private fun handleDiceCombinationClick(index: Int){
        val dice = gameState.diceList[index]
        //Checks if the dice is in the current combination
        if(!dice.inCombination){
            dice.inCurrentCombination = true
            dice.inCombination = true
            gameState.currentCombination.addToCombinationAndScore(dice.value, index)
            updateDiceImage(index, dice.value, dice.isSelected, dice.inCombination)
        } else {
            //If dice is in currentCombination, remove it from currentCombination and update UI
            if (dice.inCurrentCombination){
                gameState.currentCombination.removeFromCombinationAndScore(dice.value, index)
                resetDiceStatus(dice)
            }
        }
    }

    /**
     * PRE: gameState.combinationMode = true
     * Sets dice inCombination status to false for all dice in currentCombination.
     * Gives currentCombination a freshed initialization.
     */
    private fun startNewCombination() {
        for (index in gameState.currentCombination.diceIndecies){
            gameState.diceList[index].inCurrentCombination = false
        }
        gameState.currentCombination = Combination()
    }

    /**
     * Toggles combinationMode. Updates UI.
     */
    private fun toggleCombinationMode() {
        gameState.combinationMode = !gameState.combinationMode
        toggleCombinationDisplay()
    }

    /**
     * Adds combination to combinationList if score is above 0, i.e. if the combination is not empty.
     */
    private fun addCombinationToCombinationList(combination: Combination) {
        if (combination.score > 0) {
            gameState.combinationList.add(combination)
            gameState.currentRoundScore += combination.calculateScoreForTarget(gameState.targetScore)
            updateCombinationScoreDisplay()
            updateCombinationsList()
        } //Else throw away combination?
    }

    /**
     * Handles click event to delete a combination from the combination list.
     */
    fun handleRemoveCombinationClick(clickedCombination: Combination) {
        removeCombinationFromCombinationList(clickedCombination)
        updateCombinationsList()
        Log.d("Combination", "Combination removed: $clickedCombination")
    }

    /**
     * Removes the used combination from the list of combinations (target scores) used in the Spinner.
     */
    private fun removeUsedCombination() {
        if (gameState.targetScore == 3) {
            removeCombinationFromSpinner("Low")
        } else {
            removeCombinationFromSpinner(gameState.targetScore.toString())
        }
    }

    /**
     * Removes combination from the combination list and updates UI. Resets the dice UI of the affected dice.
     */
    private fun removeCombinationFromCombinationList(combination: Combination) {
        gameState.combinationList.remove(combination)
        updateCurrentRoundScore()
        updateCombinationsList()
        for (diceIndex in combination.diceIndecies) {
            resetDiceStatus(gameState.diceList[diceIndex])
        }
    }

    /**
     * Handles click event for the mark combination button. Sets the target score for the current round.
     */
    fun handleCombinationSelect(selectedCombination: String?) {
        gameState.targetScore = when (selectedCombination) {
            "Low" -> { 3 }
            "4" -> { 4 }
            "5" -> { 5 }
            "6" -> { 6 }
            "7" -> { 7 }
            "8" -> { 8 }
            "9" -> { 9 }
            "10" -> { 10 }
            "11" -> { 11 }
            "12" -> { 12 }
            else -> { 0 }
        }
        updateCurrentRoundScore()
    }

    /**
     * Adds currentRoundScore to the selected targetScore.
     */
    private fun addScoreToCurrentGame(currentRoundScore: Int) {
        when (gameState.targetScore) {
            3 -> { gameState.score.low = currentRoundScore }
            4 -> { gameState.score.four = currentRoundScore }
            5 -> { gameState.score.five = currentRoundScore }
            6 -> { gameState.score.six = currentRoundScore }
            7 -> { gameState.score.seven = currentRoundScore }
            8 -> { gameState.score.eight = currentRoundScore }
            9 -> { gameState.score.nine = currentRoundScore }
            10 -> { gameState.score.ten = currentRoundScore }
            11 -> { gameState.score.eleven = currentRoundScore }
            12 -> { gameState.score.twelve = currentRoundScore }
        }
    }

    /****************************************
     **         gameView functions         **
     ****************************************/
    /* Calling UI updates and changes through gameView into GameActivity. */

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
