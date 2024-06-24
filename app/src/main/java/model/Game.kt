package model

import androidx.lifecycle.ViewModel

/**
 * Game class stores game state. Also used in resuming a game, when the app reenters the foreground.
 * currentRound : Current round number
 * combinationStringList : List of combinations for the Spinner
 * combinationMode : Sets the combination mode: User can select a combination by pressing the Dice.
 * currentCombination : Current combination of selected dice, from using the "New Combination"-button until pressing "Done", the marked Dice are in currentCombination.
 * targetScore : Selected combination from the Spinner sets the target score.
 * currentRoundScore : Score of the current round, based on targetScore.
 */
class Game : ViewModel() {
    fun load(state: Game) {
        this.currentRound = state.currentRound
        this.remainingThrows = state.remainingThrows
        this.isGameOver = state.isGameOver
        this.score = state.score
        this.combinationStringList = state.combinationStringList
        this.diceList = state.diceList
        this.combinationMode = state.combinationMode
        this.combinationList = state.combinationList
        this.currentCombination = state.currentCombination
        this.targetScore = state.targetScore
        this.currentRoundScore = state.currentRoundScore
    }

    var currentRound : Int = 0
    var remainingThrows : Int = 3
    var isGameOver : Boolean = false
    var score : Score = Score()
    var combinationStringList : MutableList<String> = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    var diceList: List<Dice> = listOf(
        Dice(), //Dice1
        Dice(), //Dice2
        Dice(), //Dice3
        Dice(), //Dice4
        Dice(), //Dice5
        Dice(), //Dice6
    )
    var combinationMode: Boolean = false
    var combinationList: MutableList<Combination> = mutableListOf()
    var currentCombination: Combination = Combination()
    var targetScore: Int = 0
    var currentRoundScore: Int = 0
}
