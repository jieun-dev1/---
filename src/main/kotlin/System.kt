import PlayerImplementation.AIv2
import PlayerImplementation.GameOperator
import PlayerImplementation.V3.*

class System(
    private val gameOperator: GameOperator
) {
    //한 게임 당 10회 라운드를 진행
    fun game(): Result {
        val systemNumber = gameOperator.generateNumber()
        var round = 0
        println("\n ======(게임 시작) 시스템 숫자를 생성하였습니다: ${systemNumber} ======")
        gameOperator.initGame()
        while(round < 10) {
            round += 1
            val userInput = gameOperator.getUserInput(round) //123
            val result = gameOperator.score(userInput, systemNumber)
            val resultForPlayer = RoundResultForPlayer(
                strikeCount = result.strikeCount,
                ballCount = result.ballCount,
                attemptNum = userInput,
                round = round
            )
            gameOperator.updateCurrentResultForPlayer(resultForPlayer)

            if (result.strikeCount == 3) {
                return Result(true, round)
            }
        }
        return Result(false, 0)
    }

}

fun main(args: Array<String>) {


//    val player = AIv3(
//        patternProcessor = PatternProcessor(),
//        inputGenerator = InputGenerator(),
//        resultHitChecker = ResultHitChecker(),
//        strikeFinder = StrikeFinder(),
//        checker = Checker()
//    )

    val player = AIv2()

    val system = System(GameOperator(player, ScoringSystem()))
    // 게임 실행 횟수 (게임 당 10 라운드)
    val totalCount = 5000
    var winCount = 0
    val meanOfWonAtTry: Double //승리 시 평균적으로 소요된 라운드
    var sumOfWonAtTry = 0 //meanOfWonAtTry를 만들기 위해 합 계산

    for(i in 1..totalCount){
        val result = system.game()
        if(result.won) {
            winCount += 1
            sumOfWonAtTry += result.wonAtTry
        }
        player.initPattern()
        println("\n--${result} 라운드 결과 값 입니다--")
    }
    meanOfWonAtTry = if (winCount == 0){
        0.0
    } else {
        sumOfWonAtTry.toDouble()/winCount.toDouble()
    }
    val failCount: Int = totalCount-winCount
    val winRate: Double = (winCount.toDouble()/totalCount.toDouble())
    println("\n ===== 결과 요약 =====")
    println(
            " 총 횟수 ${totalCount}\n" +
            " 승리 횟수 ${winCount}\n" +
            " 실패 횟수 ${failCount}\n" +
            " 평균 시도 횟수 ${meanOfWonAtTry}\n" +
            " 승률 ${winRate}"
    )
}