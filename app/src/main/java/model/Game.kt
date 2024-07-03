package model

import androidx.lifecycle.SavedStateHandle
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
class Game(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var currentRound: Int
        get() = savedStateHandle["currentRound"] ?: 0
        set(value) {
            savedStateHandle["currentRound"] = value
        }

    var remainingThrows: Int
        get() = savedStateHandle["remainingThrows"] ?: 3
        set(value) {
            savedStateHandle["remainingThrows"] = value
        }

    var isGameOver: Boolean
        get() = savedStateHandle["isGameOver"] ?: false
        set(value) {
            savedStateHandle["isGameOver"] = value
        }

    var score: Score
        get() = savedStateHandle.get<Map<String, Any>>("score")?.let { Score.fromMap(it) } ?: Score()
        set(value) {
            savedStateHandle["score"] = value.toMap()
        }

    var combinationStringList: MutableList<String>
        get() = savedStateHandle["combinationStringList"] ?: mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        set(value) {
            savedStateHandle["combinationStringList"] = value
        }

    var diceList: List<Dice>
        get() = savedStateHandle.get<List<Map<String, Any>>>("diceList")?.map { Dice.fromMap(it) } ?: listOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice())
        set(value) {
            savedStateHandle["diceList"] = value.map { it.toMap() }
        }

    var combinationMode: Boolean
        get() = savedStateHandle["combinationMode"] ?: false
        set(value) {
            savedStateHandle["combinationMode"] = value
        }

    var combinationList: MutableList<Combination>
        get() = savedStateHandle.get<List<Map<String, Any>>>("combinationList")?.map { Combination.fromMap(it) }?.toMutableList() ?: mutableListOf()
        set(value) {
            savedStateHandle["combinationList"] = value.map { it.toMap() }.toMutableList()
        }

    var currentCombination: Combination
        get() = savedStateHandle.get<Map<String, Any>>("currentCombination")?.let { Combination.fromMap(it) } ?: Combination()
        set(value) {
            savedStateHandle["currentCombination"] = value.toMap()
        }

    var targetScore: Int
        get() = savedStateHandle["targetScore"] ?: 0
        set(value) {
            savedStateHandle["targetScore"] = value
        }

    var currentRoundScore: Int
        get() = savedStateHandle["currentRoundScore"] ?: 0
        set(value) {
            savedStateHandle["currentRoundScore"] = value
        }
}
