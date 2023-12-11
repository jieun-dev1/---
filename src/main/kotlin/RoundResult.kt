data class RoundResult(
    val strikeCount: Int,
    val ballCount: Int
) {
    override fun toString(): String {
        return "${strikeCount}스트라이크 ${ballCount}볼"
    }
}
