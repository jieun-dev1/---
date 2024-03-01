package PlayerImplementation.V3

/**
 * 세 자리 숫자의 자릿수를 바꿔서 나올 수 있는 경우는 5가지 이다 (원래 숫자 제외)
 * 5가지 패턴으로 원래 숫자를 변경해서, 숫자와 위치를 맞추는 전략이다.
 */
class PatternProcessor{

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