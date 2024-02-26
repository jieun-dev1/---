data class Result(
    val won: Boolean,
    val wonAtTry: Int
)

data class RoundResultForCheck(
    val strikeCount: Int,
    val ballCount: Int,
    var checked: Boolean = false
) {
    override fun toString(): String {
        return "${strikeCount}스트라이크 ${ballCount}볼"
    }
}

data class RoundResultForPlayer(
    val strikeCount: Int,
    val ballCount: Int,
    val attemptNum: List<Int>,
    val round: Int
) {
    fun isNothing(): Boolean {
        return this.strikeCount == 0 && this.ballCount == 0
    }
}

