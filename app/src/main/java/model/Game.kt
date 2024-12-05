package model

import android.util.Log
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
 *
 * UPDATE:
 * Utilizing savedStateHandle to store and retrieve game state.
 * Helper functions.
 */
class Game(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    init {
        loadState()
        Log.d("Game init", "Game initialized, loadState")
    }

    private fun loadState() {
        Log.d("Game loadState", "Game loaded")
        // Load values from SavedStateHandle and initialize properties
        val currentRound        = savedStateHandle.get<Int>("currentRound") ?: 0
        val remainingThrows     = savedStateHandle.get<Int>("remainingThrows") ?: 3
        val combinationMode     = savedStateHandle.get<Boolean>("combinationMode") ?: false
        val targetScore         = savedStateHandle.get<Int>("targetScore") ?: 0

        val score               = savedStateHandle.get<Score>("score") ?: Score()
        val diceList            = savedStateHandle.get<List<Dice>>("diceList") ?: listOf(
            Dice(), Dice(), Dice(), Dice(), Dice(), Dice()
        )
        val combinationList     = savedStateHandle.get<MutableList<Combination>>("combinationList") ?: mutableListOf()
        val currentCombination  = savedStateHandle.get<Combination>("currentCombination") ?: Combination()

        val currentRoundScore   = currentCombination.calculateScoreForTarget(targetScore)
        val combinationStringList = savedStateHandle.get<List<String>>("combinationStringList") ?: listOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        // Update SavedStateHandle with loaded values (or defaults)
        savedStateHandle["currentRound"]        = currentRound
        savedStateHandle["remainingThrows"]     = remainingThrows
        savedStateHandle["combinationMode"]     = combinationMode
        savedStateHandle["targetScore"]         = targetScore

        savedStateHandle["score"]               = score
        savedStateHandle["diceList"]            = diceList
        savedStateHandle["combinationList"]     = combinationList
        savedStateHandle["currentCombination"]  = currentCombination
        savedStateHandle["currentRoundScore"]   = currentRoundScore
        savedStateHandle["combinationStringList"] = combinationStringList

        Log.d(
            "Game loadState",
            "Round: $currentRound, " +
                    "rT: $remainingThrows, " +
                    "cM: $combinationMode, " +
                    "tS: $targetScore, " +
                    "cRS: $currentRoundScore"
        )
    }

    private fun saveState() {
        Log.d("Game saveState", "Game saved")
        savedStateHandle["currentRound"]        = savedStateHandle.get<Int>("currentRound") ?: 0
        savedStateHandle["remainingThrows"]     = savedStateHandle.get<Int>("remainingThrows") ?: 3
        savedStateHandle["combinationMode"]     = savedStateHandle.get<Boolean>("combinationMode") ?: false
        savedStateHandle["targetScore"]         = savedStateHandle.get<Int>("targetScore") ?: 0
        savedStateHandle["currentRoundScore"]   = savedStateHandle.get<Int>("currentRoundScore") ?: 0

        savedStateHandle["score"] = savedStateHandle.get<Score>("score") ?: Score()
        savedStateHandle["diceList"] = savedStateHandle.get<List<Dice>>("diceList") ?: listOf(
            Dice(), Dice(), Dice(), Dice(), Dice(), Dice(),
        )
        savedStateHandle["combinationList"]     = savedStateHandle.get<MutableList<Combination>>("combinationList") ?: mutableListOf()
        savedStateHandle["currentCombination"]  = savedStateHandle.get<Combination>("currentCombination") ?: Combination()

        savedStateHandle["combinationStringList"] = savedStateHandle.get<List<String>>("combinationStringList") ?: listOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        Log.d(
            "Game saveState",
            "Round: ${savedStateHandle.get<Int>("currentRound")} " +
                "rT: ${savedStateHandle.get<Int>("remainingThrows")}, " +
                "cM: ${savedStateHandle.get<Boolean>("combinationMode")}, " +
                "tS: ${savedStateHandle.get<Int>("targetScore")}, " +
                "cRS: ${savedStateHandle.get<Int>("currentRoundScore")}"
        )
    }
    override fun onCleared(){
        super.onCleared()
        saveState()
    }

    fun getCurrentRound(): Int          = savedStateHandle.get<Int>("currentRound") ?: 1
    fun getRemainingThrows(): Int       = savedStateHandle.get<Int>("remainingThrows") ?: 3
    fun isCombinationMode(): Boolean    = savedStateHandle.get<Boolean>("combinationMode") ?: false
    fun getTargetScore(): Int           = savedStateHandle.get<Int>("targetScore") ?: 0
    fun getCurrentRoundScore(): Int     = savedStateHandle.get<Int>("currentRoundScore") ?: 0
    fun getScore(): Score               = savedStateHandle.get<Score>("score") ?: Score()
    fun getDiceList(): List<Dice>       = savedStateHandle.get<List<Dice>>("diceList") ?: listOf(
        Dice(), Dice(), Dice(), Dice(), Dice(), Dice()
    )

    fun getDiceAtIndex( index : Int) : Dice {
        val diceList = getDiceList()
        return diceList[index]
    }
    fun getCombinationList(): MutableList<Combination> = savedStateHandle.get<MutableList<Combination>>("combinationList") ?: mutableListOf()
    fun getCurrentCombination(): Combination = savedStateHandle.get<Combination>("currentCombination") ?: Combination()
    fun setCurrentRound(value: Int) {
        savedStateHandle["currentRound"] = value
    }
    fun setCurrentRoundScore(value : Int) {
        savedStateHandle["currentRoundScore"] = value
    }
    fun setTargetScore(value: Int) {
        savedStateHandle["targetScore"] = value
    }
    fun setRemainingThrows(value: Int) {
        savedStateHandle["remainingThrows"] = value
    }
    fun setCombinationMode(value: Boolean) {
        savedStateHandle["combinationMode"] = value
    }
    fun getCombinationStringList(): List<String> = savedStateHandle.get<List<String>>("combinationStringList") ?: listOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")

    fun setCombinationStringList(value: List<String>) {
        savedStateHandle["combinationStringList"] = value
    }
    fun setCurrentCombination(value: Combination) {
        savedStateHandle["currentCombination"] = value
    }

}
