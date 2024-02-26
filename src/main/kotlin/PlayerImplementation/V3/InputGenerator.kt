package PlayerImplementation.V3

/**
 * 역할: nothing 을 제외한 숫자, 패턴을 통해 최적화한 숫자 등을 생성
 * Strike 가 있을 경우, 고정하고 num 을 만든다.
 */
class InputGenerator {

    fun randomInts(nothings: Set<Int>, fixedStrikes: List<Pair<Int, Int>>? = emptyList()): List<Int> {
        val nums = MutableList(3) { -1 }
        fixedStrikes?.forEach { (index, value) ->
            nums[index] = value
        }

        val remainingNumbers = (0..9).filterNot {
            it in nothings || it in nums
        }.shuffled()

        print("\n 현재 남아있는 remainingNumbers: $remainingNumbers \n")

        //1스트라이크일 때, index -1 out of bounds for length 1 이슈
        //아마 remainingNumbers 가 3이 안되서 이런거 아닐까?
        var remainingCnt = remainingNumbers.size
        for (i in nums.indices) {
            if (nums[i] == -1) {
                nums[i] = remainingNumbers[remainingCnt-1]
                remainingCnt -- //개선포인트..?
            }
        }
        return nums
    }
}
