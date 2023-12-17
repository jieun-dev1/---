class AIv3():Player {

    private val strikeMap = mutableMapOf<Int, Int>()
    private val balls = mutableSetOf<Int>()
    private val nothings = mutableSetOf<Int>()
    private val pattern = listOf("021", "102", "120", "201", "210")
    private var attempts = mutableListOf<List<Int>>()
    private var attemptsResult = mutableListOf<RoundResult?>()
    private var currentTry = 0
    private val fixedAnswer = mutableMapOf<Int, Int>()
    private var isPatternProcess: Boolean = false

    override fun getUserInput(round:Int): List<Int> {
        var lastAttempt = emptyList<Int>()
        var lastStrikeCount = 0
        var lastBallCount = 0
        var currentAttempt = emptyList<Int>()

        if(round == 1) {
            return randomInts()
        }

        lastStrikeCount = attemptsResult.last()!!.strikeCount
        if(isPatternProcess == false && lastStrikeCount == 2) {
            return processPattern()
        }else {
            return randomInts()
        }
    }

    private fun processPattern(): List<Int>{
        isPatternProcess = true

        //TODO 패턴 처리 해야함
        //총 5회를 모두 해봤다면, 한 번의 사이클 끝난 것.
        if (currentTry == 4){
            currentTry = 0
            isPatternProcess = false
        }
        return randomInts()
    }

    private fun randomInts(): List<Int>{
        var currentAttempt = (0..9).shuffled().take(3).toList()
        attempts.add(currentAttempt)
        return currentAttempt
    }
    override fun updateCurrentResult(result: RoundResultForPlayer) {
        attemptsResult.add(RoundResult(result.strikeCount, result.ballCount))
    }
}