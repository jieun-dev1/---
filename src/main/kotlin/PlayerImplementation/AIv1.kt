package PlayerImplementation

import Player
import RoundResultForPlayer

class AIv1 (
): Player {

    override fun getUserInput(round:Int): List<Int> {
        return listOf(1,3,4)
    }

    override fun updateCurrentResult(result: RoundResultForPlayer){
        TODO("Not yet implemented")

    }

    override fun initGame() {
        TODO("Not yet implemented")
    }

    override fun initPattern() {
        TODO("Not yet implemented")
    }
}