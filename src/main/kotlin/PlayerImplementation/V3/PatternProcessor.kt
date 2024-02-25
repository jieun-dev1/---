package PlayerImplementation.V3

class PatternProcessor{

    //TODO: strike 고정 해야함.- 패턴 적용하는 방법의 최적화 포인
    //ball 이라면 다른 위치로 바꿔줄 수 있지 않을까? 021이 1strike 1ball 이라면,
    private val patterns = listOf("021", "102", "120", "201", "210")

    fun run(patternTargetNum: List<Int>, currentTry: Int): List<Int> {
        return applyPatternToCandidateNumber(patternTargetNum, currentTry)
    }

    private fun applyPatternToCandidateNumber(patternTargetNum: List<Int>,
                                              currentTry: Int): List<Int> {
        val trial = patterns[currentTry-1]
        return trial.map { patternTargetNum[it.toString().toInt()] }
    }
}