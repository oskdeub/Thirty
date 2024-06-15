package model

import androidx.lifecycle.ViewModel

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