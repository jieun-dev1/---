package PlayerImplementation.V3

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