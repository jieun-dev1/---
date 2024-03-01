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
    private var tryCount = 0
    private var lastStrike = 0
    private var targetStrike = 0

    // 패턴을 바꿔가며 찾을 때, 기준이 되는 숫자와 결과이다. 패턴을 순회 하여 목표에 도달한 후에는 삭제하고, 다음 시도 때 다시 할당한다.
    private var patternTargetNumAndResult = Pair(listOf<Int>(), RoundResultForPlayer(0, 0, listOf(0, 0, 0), 0))

    //
    private var hintAttemptAndRoundResults = mutableListOf<RoundResultForPlayer>()
    private var fixedStrikes = mutableListOf<Pair<Int, Int>>()
    private var hintAllFound = false

    private fun isNothing(result: RoundResultForPlayer): Boolean {
        return result.strikeCount == 0 && result.ballCount == 0
    }

    override fun getUserInput(round: Int): List<Int> {
        return when {
            //1라운드 || 이전 결과가 nothing -> Nothing 제외 하고 Random 생성
            checker.isFirstRoundOrNothing(
                round = round,
                attemptResults = attemptResults,
                tryCount = tryCount,
                targetStrike = targetStrike,
                patternTargetNumAndResult = patternTargetNumAndResult
            ) -> getRandomNumExceptNothings(round, nothings, fixedStrikes)
            //패턴 전략을 사용 중 -> 계속 적용
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
            //패턴 5가지 경우 모두 시도 || 힌트 모두 찾음
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
                nothings.addAll(patternTargetNumAndResult.first - fixedStrikes.map { it.second }.toSet())
                initPattern()
                return getRandomNumExceptNothings(round, nothings, fixedStrikes)
            }

            else -> emptyList()
        }
    }


    private fun getRandomNumExceptNothings(
        round: Int,
        nothings: Set<Int>,
        fixedStrikes: List<Pair<Int, Int>>
    ): List<Int> {
        val result = inputGenerator.randomInts(nothings, fixedStrikes)
        attempts.add(result)
        return result
    }


    override fun updateCurrentResult(result: RoundResultForPlayer) {

        print("${result.round} 라운드에서 시도한 숫자:${result.attemptNum} 의 결과: ${result.strikeCount}스트라이크 ${result.ballCount} 볼 입니다\n")

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
                    attemptNum = result.attemptNum,
                    round = result.round
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
            )
        ) {
            print("NothingFoundAfterLastTrial \n" + "targetStrike: $targetStrike, result: $result, lastStrike: $lastStrike")

            nothings.addAll(result.attemptNum - fixedStrikes.map { it.second }.toSet())
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = result.strikeCount,
                    ballCount = 0,
                    attemptNum = result.attemptNum,
                    round = result.round
                )
            )
            return
        }

        //처음으로 nothing 이 아닌 결과 -> 이 숫자를 패턴에 적용하자
        if (checker.isTargetNumFound(
                targetStrike = targetStrike,
                patternTargetNum = patternTargetNumAndResult.first
            )
        ) {
            targetStrike = result.strikeCount + result.ballCount
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = result.strikeCount,
                    ballCount = result.ballCount,
                    attemptNum = result.attemptNum,
                    round = result.round
                )
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())
            print("\npatternTargetNumAndResult 가 정해졌습니다: $patternTargetNumAndResult \n   현재의 targetStrike : $targetStrike \n")
            resultHitChecker.initHitResultsForTheStrike(patternTargetNumAndResult.second)

            if (resultHitChecker.returnIfHintFound(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            return
        }

        //현재 진행 중인 패턴이 있을 때
        if (patternTargetNumAndResult != emptyList<Int>()) {
            print("현재 진행 중인 패턴입니다: $patternTargetNumAndResult")
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = result.strikeCount,
                    ballCount = result.ballCount,
                    attemptNum = result.attemptNum,
                    round = result.round
                )
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())

            if (resultHitChecker.returnIfHintFound(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            if (resultHitChecker.hintAllFound()) {
                hintAllFound = true
                print("## 여기서 hintAllFound 임을 확인했습니다 ##")
            }
            return
        }
    }

    //패턴에서 알아낼 수 있는 것을 다 봤을 때, 초기화.
    override fun initPattern() {
        print("\n ****** 패턴 초기화 ******")
        with(this) {
            tryCount = 0
            lastStrike = fixedStrikes.size
            targetStrike += 1
            patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0, 0, 0), round = 0))
            hintAttemptAndRoundResults = mutableListOf()
            // only initPattern has this.F
            hintAllFound = false
        }
    }

    //게임 종료 시 할당되었던 변수 초기화.
    override fun initGame() {
        with(this) {
            tryCount = 0
            lastStrike = 0
            targetStrike = 0
            patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0, 0, 0), 0))
            hintAttemptAndRoundResults = mutableListOf()

            nothings = mutableSetOf()
            attempts = mutableListOf()
            attemptResults = mutableListOf()

            fixedStrikes = mutableListOf()
        }
    }
}