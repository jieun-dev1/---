package PlayerImplementation

import Player
import RoundResultForPlayer

class Human(): Player {
    override fun getUserInput(round:Int): List<Int> {
        return listOf(1,2,3)
    }

    override fun updateCurrentResult(result: RoundResultForPlayer) {
        return
    }


    override fun initRound() {
        return
    }

    override fun initPattern() {
        return
    }

}