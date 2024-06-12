package view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import controller.GameController
import controller.GameView
import model.Combination
import se.umu.cs.seod0005.thirty.R

class GameActivity : AppCompatActivity(), GameView {
    private lateinit var gameController: GameController
    private lateinit var diceButtons: List<ImageButton>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gameController = GameController(this)
        gameController.startGame()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        initializeDiceButtons()


        val throwButton = findViewById<Button>(R.id.buttonThrow)
        throwButton.setOnClickListener {
            gameController.rollDice()
        }

        val combinationButton = findViewById<Button>(R.id.buttonCombination)
        combinationButton.setOnClickListener {
            gameController.combinationMode()
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
        val combinationSpinner = findViewById<Spinner>(R.id.combinationSpinner)
        val combinations = resources.getStringArray(R.array.combinations)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, combinations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        combinationSpinner.adapter = adapter

        combinationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCombination = combinations[position]
                gameController.handleCombinationSelect(selectedCombination)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected (if needed)
            }
        }
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

    override fun updateScoreDisplay(newScore: Int) {
        TODO("Not yet implemented")
    }

    override fun updateThrowsDisplay(throwsLeft: Int) {
        val throwsTextView = findViewById<TextView>(R.id.throwsLeftText)
        throwsTextView.text = throwsLeft.toString()

    }
}