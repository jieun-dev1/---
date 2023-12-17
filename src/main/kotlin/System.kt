import kotlin.math.*

class System(
    val player: Player
) {
    //게임을 주어진 횟수 만큼 실행
    fun runGames(){

    }

    //한 번의 10회 라운드를 진행f
    fun game(): Result {
        val systemNumber = generateNumber()
        var round = 0
        println("${systemNumber} **시스템 숫자입니다**")
        while(round < 10) {
            round += 1
            val userInput = player.getUserInput(round) //123
            println("${userInput} userInput 입니다")
            val result = score(userInput, systemNumber)
            val resultForPlayer = RoundResultForPlayer(
                strikeCount = result.strikeCount,
                ballCount = result.ballCount,
                attempts = userInput,
                round = round
            )
            player.updateCurrentResult(resultForPlayer)

            if (result.strikeCount == 3) {
                player.updateCurrentResult(resultForPlayer)
                return Result(true, round)
            }
            println(result)
        }
        return Result(false, 0)
    }

    //시스템이 한 회당 게임에 사용되는 number 를 생성
    fun generateNumber(): List<Int> {
        val randomList = (0..9).shuffled().take(3).toList()
        return randomList
    }

    fun score(userInputSplits:List<Int>,  systemInputSplits: List<Int>): RoundResult {
        //Key(index) Value 가 모두 일치하면 Strike
        //Value는 일치하는 데, key 는 다르면 strike
        val strikeCount = checkIfStrike(userInputSplits,systemInputSplits)
        val ballCount = checkIfBall(userInputSplits,systemInputSplits)

        return RoundResult(strikeCount,ballCount)
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
                continue;
            }
            if (userInputSplits[i] == systemInputSplits[j]) {
                ballCount++
            }            }
        }
        return ballCount
    }
}

fun main(args: Array<String>) {
    val player = AIv3()
    //val player = Human()
    val system = System(player)
    val totalCount = 1
    var winCount = 0
    var failCount = 0
    var meanOfWonAtTry = 0.0
    var sumOfWonAtTry = 0
    var winRate = 0.0

    for(i in 1..totalCount){
        val result = system.game()
        if(result.won) {
            winCount += 1
            sumOfWonAtTry += result.wonAtTry
        }
        println("--${result} 라운드 결과 값 입니다--")
    }
    if (winCount == 0){
        meanOfWonAtTry = 0.0
    } else {
        meanOfWonAtTry = sumOfWonAtTry.toDouble()/winCount.toDouble()
    }
    failCount =  totalCount-winCount
    winRate = (winCount.toDouble()/totalCount.toDouble())
    println("리포트 - 총 횟수 ${totalCount}\n승리 횟수 ${winCount}")
    println(
            " 실패 횟수 ${failCount}\n" +
            " 평균 시도 횟수 ${meanOfWonAtTry}\n" +
            " 승률 ${winRate}"
    )
}