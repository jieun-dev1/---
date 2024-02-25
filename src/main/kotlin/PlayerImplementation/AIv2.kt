package PlayerImplementation

import Player
import RoundResultForPlayer

class AIv2 (
): Player {

    override fun getUserInput(round: Int): List<Int> {
        return (0..9).shuffled().take(3).toList()
    }

    /**
     * 아래 3 메서드는 AIv3 용이어서 구현하지 않았음.
     * System 에서 쉽게 PlayerImplementation.AIv2 로 갈아끼울 수 있도록 Return 만 하게 함.
     *
     */
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