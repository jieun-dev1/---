class AIv1 (
):Player {

    override fun getUserInput(round:Int): List<Int> {
        return listOf(1,3,4)
    }

    override fun updateCurrentResult(result: RoundResultForPlayer){

    }
}