/*
 * KOTLIN CODEGEN BOX SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: constant-literals, boolean-literals -> paragraph 1 -> sentence 2
 * NUMBER: 11
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the function.
 * NOTE: this test data is generated by FeatureInteractionTestDataGenerator. DO NOT MODIFY CODE MANUALLY!
 * HELPERS: reflect
 */

fun `true`(): Boolean {
    return true
}

fun Boolean.`false`() = true

fun <T> T.`true`() = listOf(0).`false`()

inline fun <reified T, K> K?.`false`(x1: T = 10 as T)
        where K : List<T>,
              K : Iterable<T>,
              T : Comparable<T>,
              T : Number = false

fun box(): String? {
    if (!`true`()) return null
    if (null.`false`<Int, List<Int>>()) return null
    if (false.`true`()) return null
    if (!false.`false`()) return null
    if (Any().`true`()) return null

    if (!checkFunctionName(::`true`, "true")) return null
    if (!checkFunctionName(Boolean::`false`, "false")) return null
    if (!checkFunctionName(Boolean::`true`, "true")) return null
    if (!checkFunctionName(List<Int>::`false`, "false")) return null
    if (!checkFunctionName(Nothing?::`true`, "true")) return null

    return "OK"
}
