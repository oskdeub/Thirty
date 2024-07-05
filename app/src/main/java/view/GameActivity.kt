package view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import controller.CombinationAdapter
import controller.CombinationSpinnerAdapter
import model.Combination
import model.Dice
import model.Game
import model.Score
import se.umu.cs.seod0005.thirty.R

class GameActivity : AppCompatActivity(){

    private val gameViewModel: Game by viewModels()
    private lateinit var diceButtons: List<ImageButton>
    private lateinit var combinationSpinner: Spinner
    private val combinationsList: MutableList<Combination> = mutableListOf()
    private val combinationsAdapter = CombinationAdapter(this, combinationsList)
    private val spinnerItems: MutableList<String> = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        initializeDiceButtons()


        if (savedInstanceState == null) {
            startGame()
        } else {
            restoreGame()
        }

    }

    /**
     * Initializes the dice buttons.
     */
    private fun initializeDiceButtons() {
        diceButtons = listOf(
            findViewById<ImageButton>(R.id.dice1),
            findViewById<ImageButton>(R.id.dice2),
            findViewById<ImageButton>(R.id.dice3),
            findViewById<ImageButton>(R.id.dice4),
            findViewById<ImageButton>(R.id.dice5),
            findViewById<ImageButton>(R.id.dice6)
        )
        for(i in diceButtons.indices) {
            diceButtons[i].setOnClickListener {
                handleDiceClick(i) // Pass the index of the clicked die
            }
        }
    }

    /**
     * Initializes the views.
     */
    private fun initializeViews() {

        combinationSpinner = findViewById<Spinner>(R.id.combinationSpinner)
        val spinnerAdapter = CombinationSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = spinnerAdapter

        combinationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCombination = parent?.getItemAtPosition(position) as? String
                handleCombinationSelect(selectedCombination)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected (if needed)
            }
        }

        val listView = findViewById<ListView>(R.id.combinationListView)
        listView.adapter = combinationsAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Handle the item click here
            val clickedCombination = combinationsList[position]
            Toast.makeText(this, "Removed combination: ${clickedCombination.getCombinationString()}", Toast.LENGTH_SHORT).show()
            handleRemoveCombinationClick(clickedCombination)
        }

        val throwButton = findViewById<Button>(R.id.buttonThrow)
        throwButton.setOnClickListener {
            handleThrowButtonClick()
        }
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.isEnabled = false
        combinationButton.setOnClickListener {
            handleCombinationButtonClick()
        }
        val endRoundButton = findViewById<Button>(R.id.buttonEndRound)
        endRoundButton.isEnabled = false
        endRoundButton.setOnClickListener {
            endRound()
        }

    }

    /****************************************
     **             Round Logic            **
     ****************************************/
    /**
     *  Handles end round logic: adds score to current game. Updates UI: 'Total Score', and removes the combination that was used.
     *  Ends the game if currentRound is the last round (10).
     */
    private fun endRound() {
        addScoreToCurrentGame(gameViewModel.getCurrentRoundScore())
        addRoundScoreToTotalScore()
        updateScoreDisplay()
        removeUsedCombination()
        if(gameViewModel.isCombinationMode()){
            toggleCombinationMode()
        }

        if(gameViewModel.getCurrentRound() == 10){
            endGame(gameViewModel.getScore())
        } else {
            resetRound()
            startRound()
        }
    }
    /**
     * Restores game from a Game-class object. Updates the UI to correspond to the load.
     */
    private fun restoreGame(){
        updateCurrentRoundScore()
        updateThrowsDisplay()
        updateCombinationScoreDisplay()
        updateScoreDisplay()
        updateRoundNumberDisplay()
        updateCombinationsList()
        Log.d("gameState", "${gameViewModel.getRemainingThrows()}, ${gameViewModel.isCombinationMode()}")
        if(gameViewModel.isCombinationMode()){
            toggleCombinationDisplay()
        }
        setCombinationStringList()

        for (i in gameViewModel.getDiceList().indices) {
            val dice = gameViewModel.getDiceList()[i]
            updateDiceImage(i, dice.value, dice.isSelected, dice.inCombination)
        }

        if(gameViewModel.getRemainingThrows() == 0){
            setCombinationStageDisplay()
        }
    }

    private fun addRoundScoreToTotalScore() {
        gameViewModel.getScore().totalScore += gameViewModel.getCurrentRoundScore()
    }

    private fun resetThrows() {
        gameViewModel.setRemainingThrows(3)
    }

    /**
     * Resets round specific information and calls for UI updates. The combination inside gameState gets a fresh initialization.
     */
    private fun resetRound() {
        gameViewModel.setCurrentRoundScore(0)
        resetDice()
        resetThrows()
        setResetRoundDisplays()
        gameViewModel.setCurrentCombination(Combination())
    }

    /**
     * Increments gameState.currentRound by 1. Calls for a UI update to display the change and resets the Dice.
     */
    private fun startRound() {
        gameViewModel.setCurrentRound(gameViewModel.getCurrentRound() + 1)
        updateRoundNumberDisplay()
        resetDice()
    }

    private fun startGame() {
        startRound()
    }
    /****************************************
     **             Dice Logic             **
     ****************************************/

    /**
     * Throw-button logic. If gameState.remainingThrows is larger than 0, a dice roll takes place, remainingThrows are decreased by 1 and a UI call is made to update remaining throws display.
     * If remaningThrows then is 0, a UI call is made to display other options for the user.
     */
    private fun handleThrowButtonClick() {
        if (gameViewModel.getRemainingThrows() > 0) {
            rollDice()
            gameViewModel.setRemainingThrows(gameViewModel.getRemainingThrows() - 1)
            updateThrowsDisplay()
        }
        if (gameViewModel.getRemainingThrows() == 0) {
            setCombinationStageDisplay()
        }
    }

    /**
     * Dice roll logic. If the dice to be rolled is not selected nor in a combination, the dice will roll and call for an UI update of its image.
     */
    private fun rollDice() {
        for (i in gameViewModel.getDiceList().indices) {
            val dice = gameViewModel.getDiceList()[i]
            if (!dice.isSelected && !dice.inCombination) {
                dice.roll()
                Log.d("DiceRoll", "Dice${i + 1} rolled: ${dice.value}")
                updateDiceImage(i, dice.value, false, dice.inCombination)
            }
        }
        /*
        for (dice in gameViewModel.getDiceList()) {
            if (!dice.isSelected && !dice.inCombination) {
                dice.roll()
                Log.d("DiceRoll", "Dice${gameViewModel.getDiceList().indexOf(dice) + 1} rolled: ${dice.value}")
                updateDiceImage(
                    gameViewModel.getDiceList().indexOf(dice), dice.value, false, dice.inCombination
                )
            }
        }
        */
    }

    /**
     * Reset all dice in diceList. Calls for an UI update of the dice.
     */
    private fun resetDice() {
        /*
        for (dice in gameViewModel.getDiceList()) {
            dice.reset()
            updateDiceImage(gameViewModel.getDiceList().indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
        }
        */
        for (i in gameViewModel.getDiceList().indices) {
            val dice = gameViewModel.getDiceList()[i]
            dice.reset()
            updateDiceImage(i, dice.value, false, dice.inCombination)
        }
    }

    /**
     * If remaining throws is below 3, i.e. the player has rolled at least the first roll; this function saves a clicked dice with toggleDiceSelection().
     */
    private fun handleDiceSaveClick(index: Int) {
        if (gameViewModel.getRemainingThrows() < 3) {
            toggleDiceSelection(gameViewModel.getDiceAtIndex(index))
        }
    }

    /**
     * if dice is selected: unselect it. If dice is not selected: select it. Update the dice image.
     */
    private fun toggleDiceSelection(dice: Dice) {
        dice.isSelected = !dice.isSelected
        val index = gameViewModel.getDiceList().indexOf(dice)
        if (index == -1) {
            Log.e("DiceSelection", "Dice${index} not found in diceList")
            return
        }
        updateDiceImage(index, dice.value, dice.isSelected, dice.inCombination)
    }

    /**
     * Handles a dice being clicked. If the game is in combination mode, the dice gets added to the current combination.
     * If not in combination mode, saves the dice and marks it as selected.
     */
    private fun handleDiceClick(index: Int) {
        if (gameViewModel.isCombinationMode()) {
            handleDiceCombinationClick(index)
            Log.d(
                "Combination",
                "Dice${index + 1} added to currentCombination: ${gameViewModel.getCurrentCombination().getCombinationString()}"
            )
            return
        } else {
            handleDiceSaveClick(index)
            Log.d("Dice", "Dice${index + 1} saved: ${gameViewModel.getDiceList()[index].value}")
        }
    }

    /**
     * Resets the Dice inCombination status, and calls for the update of its UI image.
     */
    private fun resetDiceStatus(dice: Dice) {
        dice.inCombination = false
        dice.inCurrentCombination = false
        updateDiceImage(gameViewModel.getDiceList().indexOf(dice), dice.value, dice.isSelected, dice.inCombination)
    }

    /**
     * Calculates the score of the combinations in combinationList. Only accepts combination scores which are at the target score.
     * Updates the UI for Combination score.
     */
    private fun updateCurrentRoundScore() {
        gameViewModel.setCurrentRoundScore(0)
        for (combination in gameViewModel.getCombinationList()) {
            gameViewModel.setCurrentRoundScore(gameViewModel.getCurrentRoundScore() + combination.calculateScoreForTarget((gameViewModel.getTargetScore())))
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
    private fun handleCombinationButtonClick() {
        toggleCombinationMode()
        if (gameViewModel.isCombinationMode()) {
            startNewCombination()
        } else {
            addCombinationToCombinationList(gameViewModel.getCurrentCombination())
        }
    }

    /**
     * PRE: gameState.combinationMode = true
     * Handles clicking a dice while in combination mode. If Dice is not in currentCombination: Adds the dice to the current combination.
     * If Dice is in currentCombination
     */
    private fun handleDiceCombinationClick(index: Int){
        val dice = gameViewModel.getDiceAtIndex(index)
        //Checks if the dice is in the current combination
        if(!dice.inCombination){
            dice.inCurrentCombination = true
            dice.inCombination = true
            gameViewModel.getCurrentCombination().addToCombinationAndScore(dice.value, index)
            updateDiceImage(index, dice.value, dice.isSelected, dice.inCombination)
        } else {
            //If dice is in currentCombination, remove it from currentCombination and update UI
            if (dice.inCurrentCombination){
                gameViewModel.getCurrentCombination().removeFromCombinationAndScore(dice.value, index)
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
        for (index in gameViewModel.getCurrentCombination().diceIndecies){
            gameViewModel.getDiceList()[index].inCurrentCombination = false
        }
        gameViewModel.setCurrentCombination(Combination())
    }

    /**
     * Toggles combinationMode. Updates UI.
     */
    private fun toggleCombinationMode() {
        gameViewModel.setCombinationMode(!gameViewModel.isCombinationMode())
        toggleCombinationDisplay()
    }

    /**
     * Adds combination to combinationList if score is above 0, i.e. if the combination is not empty.
     */
    private fun addCombinationToCombinationList(combination: Combination) {
        if (combination.score > 0) {
            gameViewModel.getCombinationList().add(combination)
            gameViewModel.setCurrentRoundScore(gameViewModel.getCurrentRoundScore() + combination.calculateScoreForTarget(gameViewModel.getTargetScore()))
            updateCombinationScoreDisplay()
            updateCombinationsList()
        } //Else throw away combination?
    }

    /**
     * Handles click event to delete a combination from the combination list.
     */
    private fun handleRemoveCombinationClick(clickedCombination: Combination) {
        removeCombinationFromCombinationList(clickedCombination)
        updateCombinationsList()
        Log.d("Combination", "Combination removed: $clickedCombination")
    }

    /**
     * Removes the used combination from the list of combinations (target scores) used in the Spinner.
     */
    private fun removeUsedCombination() {
        if (gameViewModel.getTargetScore() == 3) {
            removeCombinationFromSpinner("Low")
        } else {
            removeCombinationFromSpinner(gameViewModel.getTargetScore().toString())
        }
    }

    /**
     * Removes combination from the combination list and updates UI. Resets the dice UI of the affected dice.
     */
    private fun removeCombinationFromCombinationList(combination: Combination) {
        gameViewModel.getCombinationList().remove(combination)
        updateCurrentRoundScore()
        updateCombinationsList()
        for (diceIndex in combination.diceIndecies) {
            resetDiceStatus(gameViewModel.getDiceList()[diceIndex])
        }
    }

    /**
     * Handles click event for the mark combination button. Sets the target score for the current round.
     */
    private fun handleCombinationSelect(selectedCombination: String?) {
        val target = when (selectedCombination) {
            "Low" -> 3
            else -> selectedCombination?.toIntOrNull() ?: 0
        }
        gameViewModel.setTargetScore(target)
        updateCurrentRoundScore()
    }

    /**
     * Adds currentRoundScore to the selected targetScore.
     */
    private fun addScoreToCurrentGame(currentRoundScore: Int) {
        val gameState = gameViewModel
        when (gameState.getTargetScore()) {
            3 -> { gameState.getScore().low = currentRoundScore }
            4 -> { gameState.getScore().four = currentRoundScore }
            5 -> { gameState.getScore().five = currentRoundScore }
            6 -> { gameState.getScore().six = currentRoundScore }
            7 -> { gameState.getScore().seven = currentRoundScore }
            8 -> { gameState.getScore().eight = currentRoundScore }
            9 -> { gameState.getScore().nine = currentRoundScore }
            10 -> { gameState.getScore().ten = currentRoundScore }
            11 -> { gameState.getScore().eleven = currentRoundScore }
            12 -> { gameState.getScore().twelve = currentRoundScore }
        }
    }

    /****************************************
     **         gameView functions         **
     ****************************************/
    /* Calling UI updates and changes through gameView into GameActivity. */

    private fun setResetRoundDisplays() {

        updateThrowsDisplay(gameViewModel.getRemainingThrows())
        updateCombinationScoreDisplay(0)
        updateMarkCombinationButtonEnabled(false)
        updateThrowButtonEnabled(true)
        updateEndRoundButtonEnabled(false)
        gameViewModel.getCombinationList().clear()
        updateCombinationsList(gameViewModel.getCombinationList())
    }
    private fun updateCombinationsList() {
        updateCombinationsList(gameViewModel.getCombinationList())
    }

    private fun updateThrowsDisplay() {
        updateThrowsDisplay(gameViewModel.getRemainingThrows())
    }

    private fun setCombinationStageDisplay() {
        updateMarkCombinationButtonEnabled(true)
        updateThrowButtonEnabled(false)
        updateEndRoundButtonEnabled(true)
    }

    private fun updateCombinationScoreDisplay() {
        updateCombinationScoreDisplay(gameViewModel.getCurrentRoundScore())
    }

    private fun toggleCombinationDisplay() {
        updateMarkCombinationDisplay(gameViewModel.isCombinationMode())
    }

    private fun setCombinationStringList() {
        setCombinationSpinnerItems(gameViewModel.getCombinationStringList())
    }

    private fun updateScoreDisplay() {
        updateScoreDisplay(gameViewModel.getScore().totalScore)
    }

    private fun updateRoundNumberDisplay() {
        updateRoundNumberDisplay(gameViewModel.getCurrentRound())
    }

    /**
     * Removes a combination from the Spinner.
     */
    private fun removeCombinationFromSpinner(combination: String) {
        var combinationStringList : List<String> = gameViewModel.getCombinationStringList()
        combinationStringList -= combination //Remove combination from the list (creates a new list without the combination)
        gameViewModel.setCombinationStringList(combinationStringList)

        combinationSpinner = findViewById<Spinner>(R.id.combinationSpinner)
        val spinnerAdapter = CombinationSpinnerAdapter(this, android.R.layout.simple_spinner_item, combinationStringList.toMutableList())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = spinnerAdapter

    }

    /**
     * Sets the Spinner items.
     */
    private fun setCombinationSpinnerItems(combinations: List<String>) {
        spinnerItems.clear()
        spinnerItems.addAll(combinations)

        val spinnerAdapter = CombinationSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = spinnerAdapter
    }

    /**
     * Ends the game. Gets a score array from the Game class, and Starts the ResultActivity.
     */
    private fun endGame(score : Score) {
        val intent = Intent(this, ResultActivity::class.java)

        val scoreArray = score.toIntArray()
        intent.putExtra("scoreArray", scoreArray)

        startActivity(intent)
    }

    /**
     * Updates the combinations list.
     */
    private fun updateCombinationsList(combinations: MutableList<Combination>) {
        combinationsList.clear()
        combinationsList.addAll(combinations)
        //Adepter implemented w/ Gemini
        combinationsAdapter.notifyDataSetChanged()
    }

    /**
     * Updates the combination mode display. If inCombinationMode: "Done", else: "New Combination".
     */
    private fun updateMarkCombinationDisplay(inCombinationMode: Boolean) {
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.text = if (inCombinationMode) "Done" else "New Combination"
    }

    /**
     * Updates the combinationScoreDisplay with combinationScore.
     */
    private fun updateCombinationScoreDisplay(combinationScore: Int) {
        val combinationScoreText = findViewById<TextView>(R.id.combinationScoreText)
        combinationScoreText.text = combinationScore.toString()
    }

    /**
     * Toggles throwButton.
     */
    private fun updateThrowButtonEnabled(enabled: Boolean) {
        val throwButton = findViewById<Button>(R.id.buttonThrow)
        throwButton.isEnabled = enabled
    }

    /**
     * Toggles combinationButton.
     */
    private fun updateMarkCombinationButtonEnabled(enabled: Boolean) {
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.isEnabled = enabled
    }

    /**
     * toggles endRoundButton.
     */
    private fun updateEndRoundButtonEnabled(enabled: Boolean) {
        val endRoundButton = findViewById<Button>(R.id.buttonEndRound)
        endRoundButton.isEnabled = enabled
    }

    /**
     * Updates the round number display.
     */
    private fun updateRoundNumberDisplay(roundNumber: Int) {
        val roundNumberText = findViewById<TextView>(R.id.roundNumberText)
        roundNumberText.text = roundNumber.toString()
    }

    /**
     * Updates the score display.
     */
    private fun updateScoreDisplay(newScore: Int) {
        val scoreText = findViewById<TextView>(R.id.scoreText)
        scoreText.text = newScore.toString()
    }

    /**
     * Updates the "throws left" display.
     */
    private fun updateThrowsDisplay(throwsLeft: Int) {
        val throwButton = findViewById<Button>(R.id.buttonThrow)
        val throwButtonText = "Throw ($throwsLeft)"
        throwButton.text = throwButtonText
    }

    /**
     * Updates the dice to combination color.
     */
    private fun setDiceCombinationImage(diceIndex: Int, diceValue: Int) {
        val imageResource = when (diceValue) {
            1 -> R.drawable.red1
            2 -> R.drawable.red2
            3 -> R.drawable.red3
            4 -> R.drawable.red4
            5 -> R.drawable.red5
            6 -> R.drawable.red6
            else -> R.drawable.red1 // Default to a placeholder image if the value is invalid
        }
        diceButtons[diceIndex].setImageResource(imageResource)
    }

    /**
     * Updates the dice to standard color.
     */
    private fun setDiceImage(diceIndex: Int, diceValue: Int) {
        val imageResource = when (diceValue) {
            1 -> R.drawable.white1
            2 -> R.drawable.white2
            3 -> R.drawable.white3
            4 -> R.drawable.white4
            5 -> R.drawable.white5
            6 -> R.drawable.white6
            else -> R.drawable.white1 // Default to a placeholder image if the value is invalid
        }
        diceButtons[diceIndex].setImageResource(imageResource)
    }

    /**
     * Updaates the dice to selected color.
     */
    private fun setDiceSelectedImage(diceIndex: Int, diceValue: Int){
        val imageResource = when (diceValue) {
            1 -> R.drawable.grey1
            2 -> R.drawable.grey2
            3 -> R.drawable.grey3
            4 -> R.drawable.grey4
            5 -> R.drawable.grey5
            6 -> R.drawable.grey6
            else -> R.drawable.grey1 // Default to a placeholder image if the value is invalid
        }
        diceButtons[diceIndex].setImageResource(imageResource)
    }


    /**
     * Updates the dice image based on its state. inCombination has precedence over selected.
     */
    private fun updateDiceImage(diceIndex: Int, diceValue: Int, selected: Boolean, inCombination: Boolean ){

        if (inCombination){
            setDiceCombinationImage(diceIndex, diceValue);
            return
        }
        if (!selected){
            setDiceImage(diceIndex, diceValue);
            return

        } else {
            setDiceSelectedImage(diceIndex, diceValue)
            return
        }
    }
}
