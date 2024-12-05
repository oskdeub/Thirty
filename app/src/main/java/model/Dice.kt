package model

import android.os.Parcel
import android.os.Parcelable

/**
 * value : Dice value
 * isSelected : Marks the Dice as selected.
 * inCombination : Marks the Dice as part of a combination.
 * inCurrentCombination : Marks the Dice as in currentCombination (used in Game class).
 */
class Dice : Parcelable {
    var value: Int = 0
    var isSelected : Boolean = false
    var inCombination : Boolean = false
    var inCurrentCombination : Boolean = false

    constructor(){}

    private constructor(parcel: Parcel) {
        value = parcel.readInt()
        isSelected = parcel.readByte() != 0.toByte()
        inCombination = parcel.readByte() != 0.toByte()
        inCurrentCombination = parcel.readByte() != 0.toByte()
    }

    /**
     * Resets the Dice to its initial state.
     */
    fun reset() {
        value = 0
        isSelected = false
        inCombination = false
        inCurrentCombination = false
    }

    /**
     * Rolls the Dice.
     */
    fun roll(){
        value = (1..6).random()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(value)
        dest.writeByte((if (isSelected) 1 else 0).toByte())
        dest.writeByte((if (inCombination) 1 else 0).toByte())
        dest.writeByte((if (inCurrentCombination) 1 else 0).toByte())
    }

    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }
}
