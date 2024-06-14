package view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import controller.CombinationAdapter
import controller.CombinationSpinnerAdapter
import controller.GameController
import controller.GameView
import model.Combination
import se.umu.cs.seod0005.thirty.R

class GameActivity : AppCompatActivity(), GameView {
    private lateinit var gameController: GameController
    private lateinit var diceButtons: List<ImageButton>
    private lateinit var combinationSpinner: Spinner
    private val combinationsList: MutableList<Combination> = mutableListOf()
    private val combinationsAdapter = CombinationAdapter(this, combinationsList)
    val spinnerItems: MutableList<String> = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
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

        gameController = GameController(this)
        gameController.startGame()

        val throwButton = findViewById<Button>(R.id.buttonThrow)
        throwButton.setOnClickListener {
            gameController.handleThrowButtonClick()
        }
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.isEnabled = false
        combinationButton.setOnClickListener {
            gameController.handleCombinationButtonClick()
        }
        val endRoundButton = findViewById<Button>(R.id.buttonEndRound)
        endRoundButton.isEnabled = false
        endRoundButton.setOnClickListener {
            gameController.endRound()
        }

    }
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
                gameController.handleDiceClick(i) // Pass the index of the clicked die
            }
        }
    }
    private fun initializeViews() {

        combinationSpinner = findViewById<Spinner>(R.id.combinationSpinner)
        val spinnerAdapter = CombinationSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = spinnerAdapter

        combinationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCombination = spinnerItems[position]
                gameController.handleCombinationSelect(selectedCombination)
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
            Toast.makeText(this, "Removed combination: ${clickedCombination.combination}", Toast.LENGTH_SHORT).show()
            gameController.handleRemoveCombinationClick(clickedCombination)
        }
    }
    override fun removeCombinationFromSpinner(combination: String) {
        spinnerItems.remove(combination)
        combinationSpinner = findViewById<Spinner>(R.id.combinationSpinner)
        val spinnerAdapter = CombinationSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = spinnerAdapter

    }
    override fun updateCombinationsList(combinations: MutableList<Combination>) {
        combinationsList.clear()
        combinationsList.addAll(combinations)
        //Adepter implemented w/ Gemini
        combinationsAdapter.notifyDataSetChanged()
    }

    override fun updateMarkCombinationDisplay(inCombinationMode: Boolean) {
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.text = if (inCombinationMode) "Done" else "New Combination"
    }

    override fun updateRoundScoreDisplay(roundScore: Int) {
        TODO("Not yet implemented")
    }

    override fun updateCombinationScoreDisplay(combinationScore: Int) {
        val combinationScoreText = findViewById<TextView>(R.id.combinationScoreText)
        combinationScoreText.text = combinationScore.toString()
    }

    override fun updateThrowButtonEnabled(enabled: Boolean) {
        val throwButton = findViewById<Button>(R.id.buttonThrow)
        throwButton.isEnabled = enabled
    }

    override fun updateMarkCombinationButtonEnabled(enabled: Boolean) {
        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.isEnabled = enabled
    }

    override fun updateEndRoundButtonEnabled(enabled: Boolean) {
        val endRoundButton = findViewById<Button>(R.id.buttonEndRound)
        endRoundButton.isEnabled = enabled
    }

    override fun updateRoundNumberDisplay(roundNumber: Int) {
        val roundNumberText = findViewById<TextView>(R.id.roundNumberText)
        roundNumberText.text = roundNumber.toString()
    }

    override fun updateScoreDisplay(newScore: Int) {
        val scoreText = findViewById<TextView>(R.id.scoreText)
        scoreText.text = newScore.toString()
    }

    override fun updateThrowsDisplay(throwsLeft: Int) {
        val throwButton = findViewById<Button>(R.id.buttonThrow)
        val throwButtonText = "Throw ($throwsLeft)"
        throwButton.text = throwButtonText
    }

    override fun updateDiceImage(diceIndex: Int, diceValue: Int, selected: Boolean, inCombination: Boolean ){

        if (inCombination){
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
            return
        }
        if (!selected){
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

        } else {
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
    }


}