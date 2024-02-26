package PlayerImplementation

import Player
import PlayerImplementation.V3.ScoringSystem
import RoundResultForCheck
import RoundResultForPlayer


class GameOperator(
    private val player: Player,
    private val scoringSystem: ScoringSystem
) {

    //시스템이 한 회당 게임에 사용되는 number 를 생성
    fun generateNumber(): List<Int> {
        val randomList = (0..9).shuffled().take(3).toList()
        return randomList
    }

    fun initGame() {
        player.initGame()
    }

    fun getUserInput(round: Int): List<Int> {
        return player.getUserInput(round)
    }

    fun score(userInput: List<Int>, systemNumber: List<Int>): RoundResultForCheck {
        return scoringSystem.score(userInput, systemNumber)
    }

    fun updateCurrentResultForPlayer(resultForPlayer: RoundResultForPlayer) {
        player.updateCurrentResult(resultForPlayer)
    }

}