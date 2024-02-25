package PlayerImplementation.V3

import RoundResultForPlayer

class Checker {
    fun isFirstRoundOrNothing(
        round: Int,
        attemptResults: List<RoundResultForPlayer>,
        tryCount: Int,
        targetStrike: Int,
        patternTargetNumAndResult: Pair<List<Int>, RoundResultForPlayer>
    ): Boolean {
        return round == 1 || attemptResults.last().isNothing() ||
                tryCount == 0 && targetStrike < 1 || patternTargetNumAndResult.first == emptyList<Int>()
    }

    fun isPatternApplicationNeeded(
        hintAllFound: Boolean,
        tryCount: Int,
        patternTargetNumAndResult: Pair<List<Int>, RoundResultForPlayer>
    ): Boolean {
        return (patternTargetNumAndResult.first != emptyList<Int>())
                && tryCount <= 4 && !hintAllFound
    }

    fun isPatternAllTriedOrHintAllFound(
        tryCount: Int,
        hintAllFound: Boolean
    ) = (tryCount == 5) || hintAllFound

    fun isNothingFoundAfterLastTrial(
        targetStrike: Int,
        result: RoundResultForPlayer,
        lastStrike: Int
    ): Boolean {
        return (targetStrike!=0) && (result.strikeCount == lastStrike)
                && result.ballCount == 0
    }

    fun isTargetNumFound(
        targetStrike: Int, patternTargetNum: List<Int>) =
        targetStrike != 0 || (patternTargetNum == emptyList<Int>())

}