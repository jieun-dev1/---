package PlayerImplementation.V3

import RoundResultForCheck
import RoundResultForPlayer

class ResultHitChecker {

    private var results:List<RoundResultForCheck> = emptyList()

    fun returnIfHintFound(roundResult: RoundResultForPlayer): RoundResultForPlayer? {
        val found =  results.find {
            (roundResult.strikeCount == it.strikeCount)
            && (roundResult.ballCount == it.ballCount)
            && !it.checked
        }

        if (found != null) {
            found.checked = true
            return RoundResultForPlayer(
                strikeCount = found.strikeCount,
                ballCount = found.ballCount,
                attemptNum = roundResult.attemptNum,
                round = roundResult.round
            )
        }
        return null
    }

    fun initHitResultsForTheStrike(roundResult: RoundResultForPlayer){
        when (roundResult.strikeCount + roundResult.ballCount) {
            1 -> {
                this.results = listOf(
                    RoundResultForCheck(1,0, false),
                    RoundResultForCheck(1,0, false)
                )
            }
            2 -> {
                this.results = listOf(
                    RoundResultForCheck(1,1, false),
                    RoundResultForCheck(2,0, false),
                    RoundResultForCheck(1,1, false)
                )
            }
            3 -> {
                this.results = listOf(
                    RoundResultForCheck(0,3,false),
                    RoundResultForCheck(0,3,false)
                )
            }
        }
    }

    fun hintAllFound(): Boolean {
        return this.results.all { it.checked }
    }

}