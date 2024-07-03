package model

/**
 * Score class. Keeps track of all scores for each combination.
 */
class Score {
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

    /**
     * Returns an intArray of all scores.
     */
    fun toIntArray(): IntArray {
        return intArrayOf(low, four, five, six, seven, eight, nine, ten, eleven, twelve)
    }

    /**
     * Converts the Score object to a Map.
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "totalScore" to totalScore,
            "low" to low,
            "four" to four,
            "five" to five,
            "six" to six,
            "seven" to seven,
            "eight" to eight,
            "nine" to nine,
            "ten" to ten,
            "eleven" to eleven,
            "twelve" to twelve
        )
    }

    companion object {
        /**
         * Creates a Score object from a Map.
         */
        fun fromMap(map: Map<String, Any>): Score {
            val score = Score()
            score.totalScore = map["totalScore"] as Int
            score.low = map["low"] as Int
            score.four = map["four"] as Int
            score.five = map["five"] as Int
            score.six = map["six"] as Int
            score.seven = map["seven"] as Int
            score.eight = map["eight"] as Int
            score.nine = map["nine"] as Int
            score.ten = map["ten"] as Int
            score.eleven = map["eleven"] as Int
            score.twelve = map["twelve"] as Int
            return score
        }
    }
}
