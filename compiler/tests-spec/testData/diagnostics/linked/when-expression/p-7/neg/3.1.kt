
/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: when-expression -> paragraph 7 -> sentence 3
 * NUMBER: 1
 * DESCRIPTION: 'When' with bound value and 'when condition' with range expression, but without containment checking operator.
 * HELPERS: basicTypes
 */

// TESTCASE NUMBER: 1
fun case_1(value_1: Int, value_2: _BasicTypesProvider): String {
    when (value_1) {
        <!INCOMPATIBLE_TYPES!>-1000L..100<!> -> return ""
        <!INCOMPATIBLE_TYPES!>value_2.getInt()..getLong()<!> -> return ""
    }

    return ""
}
