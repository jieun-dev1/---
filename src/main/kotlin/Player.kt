interface Player {
    fun getUserInput(round: Int): List<Int>

    fun updateCurrentResult(result: RoundResultForPlayer)fun initGame()

    fun initPattern()
}