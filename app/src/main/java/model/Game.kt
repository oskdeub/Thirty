package model

class Game {
    var currentRound : Int = 0
    var remainingThrows : Int = 3
    var isGameOver : Boolean = false
    var score : Score = Score()
}