package PlayerImplementation.V3

import Player
import RoundResultForPlayer

/**
 *   1. Nothing 이 아닌 Number 가 처음으로 등장했을 때 ->  candidate 으로 할당.
 *   2. currentTry 를 증가시키면서 패턴의 횟수인 5개가 될 때까지 센다.
 *   3. 패턴이 완성되면 EmptyList로 바꿔서, 다음 candidate 탐색
 *
 *    책임
 *    1. 유저 Input을 반환한다.
 *    2. Input 에 대한 result 를 기억한다.
 *    3. 현재 찾아야 하는 strike 수를 기억한다.
 *    3. 여러 패턴을 시도해서, 찾아야하는 목표인 힌트가 나온다면, 저장한다.
 *
 *    이미 1 strike 가 어딘 지 알았다면, 2스트라이크로 넘어가야 함. targetStrike 가 2 여야함.
 */
class AIv3(
    private val patternProcessor: PatternProcessor,
    private val inputGenerator: InputGenerator,
    private val resultHitChecker: ResultHitChecker,
    private val strikeFinder: StrikeFinder
) : Player {
    private var nothings = mutableSetOf<Int>()
    private var attempts = mutableListOf<List<Int>>()
    private var attemptResults = mutableListOf<RoundResultForPlayer>()

    // 패턴을 바꿔가며 찾을 때, 기준이 되는 숫자와 결과 / 패턴을 순회 하여 목표에 도달한 후에는 삭제.
    private var patternTargetNumAndResult = Pair(listOf<Int>(), RoundResultForPlayer(0, 0, listOf(0,0,0)))

    //이게 진짜 필요 할까? -- 용도: 이미 1strike 가 나왔는데
    private var lastStrike = 0
    private var tryCount = 0
    private var targetStrike = 0
    private var hintAttemptAndRoundResults = mutableListOf<RoundResultForPlayer>()
    private var fixedStrikes = mutableListOf<Pair<Int, Int>>()
    private var hintAllFound = false

    //한번 패턴이 적용되었다면, round1 도 아니고, nothing 도 아님. 여기서 적용했는데도, 변화가 없다면?
    //targetStrike 가 1 올라갔지만, 그대로 2스트라이크라면?
    override fun getUserInput(round: Int): List<Int> {

        if (round == 1 || attemptResults.last().isNothing() || (tryCount == 0 && targetStrike < 1) ||
            patternTargetNumAndResult.first == emptyList<Int>()) {
            return getRandomNumExceptNothings(round, nothings, fixedStrikes)

        } else if ((patternTargetNumAndResult.first != emptyList<Int>()) && tryCount <= 4 && !hintAllFound) {
            tryCount += 1
            return patternProcessor.run(
                patternTargetNum = patternTargetNumAndResult.first,
                currentTry = tryCount
            )

        } else if (tryCount == 5 || hintAllFound) {
            fixedStrikes.addAll(
                strikeFinder.from(
                    targetStrike = targetStrike,
                    hintAttemptAndRoundResults = hintAttemptAndRoundResults
                )
            )
            nothings.addAll(patternTargetNumAndResult.first - fixedStrikes.map {it.second}.toSet())
            initPattern()
            print("임시 로그 -- if tryCount == 5 || hintAllFound")
            //여기부터 다시
            return getRandomNumExceptNothings(round, nothings, fixedStrikes)
        }
        return emptyList()
    }

    override fun initPattern() {
        with(this) {
            tryCount = 0
            lastStrike = fixedStrikes.size
            targetStrike += 1
            patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0,0,0)))
            hintAttemptAndRoundResults = mutableListOf()
            hintAllFound = false
        }
    }

    private fun getRandomNumExceptNothings(round: Int, nothings: Set<Int>, fixedStrikes: List<Pair<Int, Int>>): List<Int> {
        val result = inputGenerator.randomInts(nothings, fixedStrikes)
        println("\n${round}라운드의 random 숫자 : ${result}\n")
        attempts.add(result)
        return result
    }

    override fun initRound() {
        this.nothings = mutableSetOf()
        this.attempts = mutableListOf()
        this.attemptResults = mutableListOf()
        this.patternTargetNumAndResult = Pair(listOf(), RoundResultForPlayer(0, 0, listOf(0,0,0)))
        this.lastStrike = 0
        this.tryCount = 0
        this.targetStrike = 0
        this.hintAttemptAndRoundResults = mutableListOf()
        this.fixedStrikes = mutableListOf()
    }

    override fun updateCurrentResult(result: RoundResultForPlayer) {

        print("시도한 숫자:${result.attemptNum} 의 결과: ${result.strikeCount}스트라이크 ${result.ballCount} 볼 입니다\n")

        if (result.strikeCount == 3) {
            return
        }

        //처음으로 nothing 일 때
        if ((result.strikeCount == 0 && result.ballCount == 0)) {
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
        //패턴은 없으나, 마지막으로 얻은 결과 == 현재 결과일 때 && targetStrike 가 존재할 때.
        //현재 fixedStrike 를 제외한 숫자는 emptyList 에 넣는다.
        if (targetStrike!=0 && result.strikeCount == lastStrike
            && result.ballCount == 0) {
            nothings.addAll(result.attemptNum - fixedStrikes.map { it.second }.toSet())
            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, 0, result.attemptNum))
            return
        }

        //처음으로 nothing 이 아닌 결과가 나왔을 때
        if (targetStrike == 0 || patternTargetNumAndResult.first == emptyList<Int>()) {
            targetStrike = result.strikeCount + result.ballCount

            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, result.ballCount, result.attemptNum)
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())
            print("patternTargetNumAndResult이 정해졌습니다: $patternTargetNumAndResult \n   현재의 targetStrike 입니ㅜ: $targetStrike\\n\")\n")
            resultHitChecker.initHitResultsForTheStrike(patternTargetNumAndResult.second)

            if (resultHitChecker.checkAndReturnResult(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            return
        }

        if (patternTargetNumAndResult == emptyList<Int>()) {
            //2Strike -> 2TargetStrike 가 아님.
            targetStrike = result.strikeCount + result.ballCount

            attemptResults.add(
                RoundResultForPlayer(result.strikeCount, result.ballCount, result.attemptNum)
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())
            print("patternTargetNumAndResult이 정해졌습니다: $patternTargetNumAndResult \n")
            resultHitChecker.initHitResultsForTheStrike(patternTargetNumAndResult.second)

            if (resultHitChecker.checkAndReturnResult(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            return
        }

        if (patternTargetNumAndResult != emptyList<Int>()) {
            attemptResults.add(
                RoundResultForPlayer(
                    strikeCount = result.strikeCount,
                    ballCount = result.ballCount,
                    attemptNum = result.attemptNum
                )
            )
            patternTargetNumAndResult = Pair(attempts.last(), attemptResults.last())

            if (resultHitChecker.checkAndReturnResult(patternTargetNumAndResult.second) != null) {
                hintAttemptAndRoundResults.add(result)
            }
            if(resultHitChecker.hintAllFound()){
                hintAllFound = true
                print("## 여기서 hintAllFound 임을 확인했습니다 ##")
            }
            return
        }
    }

}