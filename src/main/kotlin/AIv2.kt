class AIv2 (
):Player {

    override fun getUserInput(round:Int): List<Int> {
        val randomList = (0..9).shuffled().take(3).toList()
        return randomList
    }
    override fun updateCurrentResult(result: RoundResultForPlayer) {}
}