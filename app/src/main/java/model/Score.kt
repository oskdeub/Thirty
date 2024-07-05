package model

import android.os.Parcel
import android.os.Parcelable

/**
 * Score class. Keeps track of all scores for each combination.
 */
class Score : Parcelable {
    var totalScore = 0;
    var low = 0;
    var four = 0;
    var five = 0;
    var six = 0;
    var seven = 0;
    var eight = 0;
    var nine = 0;
    var ten = 0;
    var eleven = 0;
    var twelve = 0;

    constructor(){}

    private constructor(parcel: Parcel){
        totalScore = parcel.readInt()
        var scoresArray : IntArray = parcel.createIntArray() ?: intArrayOf()

        low     = scoresArray[0]
        four    = scoresArray[1]
        five    = scoresArray[2]
        six     = scoresArray[3]
        seven   = scoresArray[4]
        eight   = scoresArray[5]
        nine    = scoresArray[6]
        ten     = scoresArray[7]
        eleven  = scoresArray[8]
        twelve  = scoresArray[9]
    }

    /**
     * Returns an intArray of all scores.
     */
    fun toIntArray(): IntArray {
        return intArrayOf(low, four, five, six, seven, eight, nine, ten, eleven, twelve)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(totalScore)
        dest.writeIntArray(toIntArray())
    }

    companion object CREATOR : Parcelable.Creator<Score> {
        override fun createFromParcel(parcel: Parcel): Score {
            return Score(parcel)
        }

        override fun newArray(size: Int): Array<Score?> {
            return arrayOfNulls(size)
        }
    }
}
