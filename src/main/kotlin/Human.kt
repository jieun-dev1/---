class Human(): Player {
    override fun getUserInput(round:Int): List<Int> {
        return listOf(1,2,3)
    }

    override fun updateCurrentResult(result: RoundResultForPlayer) {
        TODO("Not yet implemented")
    }

}