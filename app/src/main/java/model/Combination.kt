package model

import android.os.Parcel
import android.os.Parcelable


/**
 * Combination class stores a combination of dice. The dice values of a combination are stored in combination. The dice indecies of a combination are stored in diceIndecies.
 * If the score is at target (selected in the combination Spinner) scoreAtTarget is true (must invoke calculateScoreForTarget().
 */
class Combination : Parcelable {
    var combination : IntArray = intArrayOf()
    var diceIndecies : IntArray = intArrayOf()
    var score : Int = 0
    var scoreAtTarget : Boolean = false

    constructor() {}

    private constructor(parcel: Parcel) {
        score = parcel.readInt()
        scoreAtTarget = parcel.readByte() != 0.toByte()
        combination = parcel.createIntArray() ?: intArrayOf()
        diceIndecies = parcel.createIntArray() ?: intArrayOf()

    }
    /**
     * Adds number to combination, and diceIndex to diceIndecies. Updates the score of the combinaiton.
     */
    fun addToCombinationAndScore(number: Int, diceIndex: Int) {
        combination += number
        diceIndecies += diceIndex
        score += number
    }

    /**
     * Calculates the score to see if it matches target. Sets scoreAtTarget to true if it does.
     */
    fun calculateScoreForTarget(target: Int) : Int {
        if (target == 3){ // Combination selected is LOW
            if(combination.sum() <= 3){
                scoreAtTarget = true
                return combination.sum()
            } else {
                scoreAtTarget = false
                return 0
            }
        }
        if (combination.sum() == target){
            scoreAtTarget = true
            return combination.sum()
        } else {
            scoreAtTarget = false
            return 0
        }
    }

    fun getCombinationString() : String{
        var combinationString : String = ""
        for (c in combination) {
            combinationString += "$c, "
        }
        //Remove the trailing comma and space
        combinationString = combinationString.removeSuffix(' '.toString())
        combinationString = combinationString.removeSuffix(','.toString())

        return combinationString
    }

    /**
     * Removes a number from combination and diceIndecies. Updates the score of the combinaiton.
     */
    fun removeFromCombinationAndScore(value: Int, index: Int) {
        val newCombination = combination.toMutableList()
        newCombination.remove(value)
        combination = newCombination.toIntArray()

        diceIndecies = diceIndecies.filter { it != index }.toIntArray()
        score -= value
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(score)
        dest.writeByte(if (scoreAtTarget) 1.toByte() else 0.toByte())
        dest.writeIntArray(combination)
        dest.writeIntArray(diceIndecies)
    }

    companion object CREATOR : Parcelable.Creator<Combination> {
        override fun createFromParcel(parcel: Parcel): Combination {
            return Combination(parcel)
        }

        override fun newArray(size: Int): Array<Combination?> {
            return arrayOfNulls(size)
        }
    }
}

