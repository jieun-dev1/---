class AIv2 (
):Player {

    override fun getUserInput(): List<Int> {
        val randomList = (0..9).shuffled().take(3).toList()
        return randomList
    }
}