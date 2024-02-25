package PlayerImplementation.V3

import Player
import RoundResultForPlayer

/**
 * TODO: 아래 클래스의 변수를 변경하는 코드가 많은데, 어떻게 다른 클래스로 위임하면서 중복을 없앨지..?
 */
class AIv3(
    private val patternProcessor: PatternProcessor,
    private val inputGenerator: InputGenerator,
    private val resultHitChecker: ResultHitChecker,
    private val strikeFinder: StrikeFinder,
    private val checker: Checker
) : Player {
    private var nothings = mutableSetOf<Int>()
    private var attempts = mutableListOf<List<Int>>()
    private var attemptResults = mutableListOf<RoundResultForPlayer>()

    //이게 진짜 필요 할까? -- 용도: 이미 1strike 가 나왔는데
    private var tryCount = 0
    private var lastStrike = 0
    private var targetStrike = 0
    // 패턴을 바꿔가며 찾을 때, 기준이 되는 숫자와 결과 / 패턴을 순회 하여 목표에 도달한 후에는 삭제.
    private var patternTargetNumAndResult = Pair(listOf<Int>(), RoundResultForPlayer(0, 0, listOf(0,0,0)))
    private var hintAttemptAndRoundResults = mutableListOf<RoundResultForPlayer>()
    private var fixedStrikes = mutableListOf<Pair<Int, Int>>()
    private var hintAllFound = false

    private fun isNothing(result: RoundResultForPlayer): Boolean {
       return result.strikeCount == 0 && result.ballCount == 0
    }


    //한번 패턴이 적용되었다면, round1 도 아니고, nothing 도 아님. 여기서 적용했는데도, 변화가 없다면?
    //targetStrike 가 1 올라갔지만, 그대로 2 스트라이크 라면?
    override fun getUserInput(round: Int): List<Int> {
        return when {
            checker.isFirstRoundOrNothing(
                round = round,
                attemptResults = attemptResults,
                tryCount = tryCount,
                targetStrike = targetStrike,
                patternTargetNumAndResult = patternTargetNumAndResult
            ) -> getRandomNumExceptNothings(round, nothings, fixedStrikes)

            checker.isPatternApplicationNeeded(
                hintAllFound = hintAllFound,
                tryCount = tryCount,
                patternTargetNumAndResult = patternTargetNumAndResult,
            ) -> {
                tryCount += 1
                patternProcessor.run(
                    patternTargetNum = patternTargetNumAndResult.first,
                    currentTry = tryCount
                )
            }

            checker.isPatternAllTriedOrHintAllFound(
                tryCount = tryCount,
                hintAllFound = hintAllFound
            ) -> {
                fixedStrikes.addAll(
                    strikeFinder.from(
                        targetStrike = targetStrike,
                        hintAttemptAndRoundResults = hintAttemptAndRoundResults
                    )
                )
                nothings.addAll(patternTargetNumAndResult.first - fixedStrikes.map {it.second}.toSet())
                initPattern()
                return getRandomNumExceptNothings(round, nothings, fixedStrikes)
            }

            else -> emptyList()
        }
        }


    private fun getRandomNumExceptNothings(round: Int, nothings: Set<Int>, fixedStrikes: List<Pair<Int, Int>>): List<Int> {
        val result = inputGenerator.randomInts(nothings, fixedStrikes)
        println("\n${round}라운드의 random 숫자 : ${result}\n")
        attempts.add(result)
        return result
    }



    override fun updateCurrentResult(result: RoundResultForPlayer) {

        print("시도한 숫자:${result.attemptNum} 의 결과: ${result.strikeCount}스트라이크 ${result.ballCount} 볼 입니다\n")

        if (result.strikeCount == 3) {
            return
        }

        //처음으로 nothing 일 때
        if (isNothing(result)) {
            nothings.addAll(result.attemptNum)
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = 0,
                    ballCount = 0,
                    attemptNum = result.attemptNum
                )
            )
            return
        }
        //마지막으로 얻은 strike 수와 == 현재 결과의 TargetStrike 가 같을 때.
        //즉, fixedStrike 를 제외한 숫자는 해당없으니, emptyList 에 넣는다.
        if (checker.isNothingFoundAfterLastTrial(
                targetStrike = targetStrike,
                result = result,
                lastStrike = lastStrike
            )) {
            print("targetStrike: $targetStrike, result: $result, lastStrike: $lastStrike \n checker.isNothingFoundAfterLastTrial 로 감 ")

            nothings.addAll(result.attemptNum - fixedStrikes.map { it.second }.toSet())
            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, 0, result.attemptNum))
            return
        }

        //처음으로 nothing 이 아닌 결과가 나와서, 이 숫자를 패턴에 적용하고자 할 때.
        if (checker.isTargetNumFound(
                targetStrike = targetStrike,
                patternTargetNum = patternTargetNumAndResult.first)
            ) {
            print("targetStrike: $targetStrike patternTargetNum: $patternTargetNumAndResult.first, \n checker.isTargetNumFound가 나옴")
            targetStrike = result.strikeCount + result.ballCount
            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, result.ballCount, result.attemptNum)
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())
            print("patternTargetNumAndResult이 정해졌습니다: $patternTargetNumAndResult \n   현재의 targetStrike 입니다: $targetStrike \n")
            resultHitChecker.initHitResultsForTheStrike(patternTargetNumAndResult.second)

            if (resultHitChecker.returnIfHintFound(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            return
        }

        if (patternTargetNumAndResult == emptyList<Int>()) {
            print("patternTargetNumAndResult == emptyList<Int>()가 나옴")

            targetStrike = result.strikeCount + result.ballCount

            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, result.ballCount, result.attemptNum)
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())
            print("patternTargetNumAndResult이 정해졌습니다: $patternTargetNumAndResult \n")
            resultHitChecker.initHitResultsForTheStrike(patternTargetNumAndResult.second)

            if (resultHitChecker.returnIfHintFound(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            return
        }

        //현재 진행 중인 패턴이 있을 때
        if (patternTargetNumAndResult != emptyList<Int>()) {
            print("patternTargetNumAndResult != emptyList<Int>() 등장")
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = result.strikeCount,
                    ballCount = result.ballCount,
                    attemptNum = result.attemptNum
                )
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())

            if (resultHitChecker.returnIfHintFound(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            if(resultHitChecker.hintAllFound()){
                hintAllFound = true
                print("## 여기서 hintAllFound 임을 확인했습니다 ##")
            }
            return
        }
    }

    override fun initPattern() {
        print("initPattern 의 호출")
        with(this) {
            tryCount = 0
            lastStrike = fixedStrikes.size
            targetStrike += 1
            patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0,0,0)))
            hintAttemptAndRoundResults = mutableListOf()
            // only initPattern has this.
            hintAllFound = false
        }
    }

    override fun initRound() {
        print("initRound 의 호출")
        with(this){
            tryCount = 0
            lastStrike = fixedStrikes.size
            targetStrike = 0
            patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0,0,0)))
            hintAttemptAndRoundResults = mutableListOf()
            nothings = mutableSetOf()
            attempts = mutableListOf()
            attemptResults = mutableListOf()
            fixedStrikes = mutableListOf()
        }


    }

}