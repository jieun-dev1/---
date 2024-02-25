import PlayerImplementation.V3.*

/**
 * attempts, attemptResults 가 있어야 하나?
 */
class System(
    private val player: Player
) {

    //한 번의 10회 라운드를 진행
    fun game(): Result {
        val systemNumber = generateNumber()
        var round = 0
        println("\n ======(게임 시작) 시스템 숫자를 생성하였습니다: ${systemNumber} ======")
        player.initRound()
        while(round < 10) {
            round += 1
            val userInput = player.getUserInput(round) //123
            val result = score(userInput, systemNumber)
            val resultForPlayer = RoundResultForPlayer(
                strikeCount = result.strikeCount,
                ballCount = result.ballCount,
                attemptNum = userInput
            )
            player.updateCurrentResult(resultForPlayer)

            if (result.strikeCount == 3) {
                return Result(true, round)
            }
        }
        return Result(false, 0)
    }

    //시스템이 한 회당 게임에 사용되는 number 를 생성
    fun generateNumber(): List<Int> {
        val randomList = (0..9).shuffled().take(3).toList()
        return randomList
    }

    fun score(userInputSplits:List<Int>,  systemInputSplits: List<Int>): RoundResultForCheck {
        //Key(index) Value 가 모두 일치하면 Strike
        //Value는 일치하는 데, key 는 다르면 strike
        val strikeCount = checkIfStrike(userInputSplits,systemInputSplits)
        val ballCount = checkIfBall(userInputSplits,systemInputSplits)

        return RoundResultForCheck(strikeCount,ballCount)
    }

    fun checkIfStrike(userInputSplits:List<Int>,  systemInputSplits: List<Int>): Int {
        var strikeCount = 0
        for (i in 0..2) {
            if (userInputSplits[i] == systemInputSplits[i]){
                strikeCount++
            }
        }
        return strikeCount
    }

    fun checkIfBall(userInputSplits:List<Int>, systemInputSplits: List<Int>): Int {
        var ballCount = 0

        for (i in 0..2) {
            for (j in 0..2){
            if (i == j){
                continue
            }
            if (userInputSplits[i] == systemInputSplits[j]) {
                ballCount++
            }            }
        }
        return ballCount
    }
}

fun main(args: Array<String>) {
    /**
     * AIv2 (전략 없이 랜덤 숫자) 로 500회 진행 시  승률 0.016 ||  평균 시도 횟수 7.875
     */
//    val player = PlayerImplementation.AIv2()

    /**의
     *  AIv3 로 500회 진행 시 0.456 승률 || 평균 시도 횟수 8.03
     */

    val player = AIv3(
        patternProcessor = PatternProcessor(),
        inputGenerator = InputGenerator(),
        resultHitChecker = ResultHitChecker(),
        strikeFinder = StrikeFinder()
    )

    val system = System(player)
    // 게임 실행 횟수 (게임 당 10 라운드)
    val totalCount = 500
    var winCount = 0
    val meanOfWonAtTry: Double
    var sumOfWonAtTry = 0

    for(i in 1..totalCount){
        val result = system.game()
        if(result.won) {
            winCount += 1
            sumOfWonAtTry += result.wonAtTry
        }
        player.initPattern()
        println("--${result} 라운드 결과 값 입니다--")
    }
    meanOfWonAtTry = if (winCount == 0){
        0.0
    } else {
        sumOfWonAtTry.toDouble()/winCount.toDouble()
    }
    val failCount: Int = totalCount-winCount
    val winRate: Double = (winCount.toDouble()/totalCount.toDouble())
    println("리포트 - 총 횟수 ${totalCount}\n승리 횟수 ${winCount}")
    println(
            " 실패 횟수 ${failCount}\n" +
            " 평균 시도 횟수 ${meanOfWonAtTry}\n" +
            " 승률 ${winRate}"
    )
}