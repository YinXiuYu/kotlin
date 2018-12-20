/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import org.junit.Assert
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File
import java.lang.Error
import java.util.regex.Matcher
import java.util.regex.Pattern

enum class TestsExceptionType(val postfix: String) {
    COMPILER_ERROR("compiler"),
    COMPILETIME_ERROR("compiletime"),
    RUNTIME_ERROR("runtime"),
    INFRASTRUCTURE_ERROR("infrastructure");

    companion object {
        private val map = values().associateBy(TestsExceptionType::postfix)

        fun fromValue(type: String) = map[type]
    }
}

sealed class TestsError(val original: Throwable, val type: TestsExceptionType) : Error() {
    override fun toString() = original.toString()
    override fun getStackTrace() = original.stackTrace
    override fun initCause(cause: Throwable?) = original.initCause(cause)
    override val cause = original.cause
}

class TestsCompilerError(original: Throwable) : TestsError(original, TestsExceptionType.COMPILER_ERROR)
class TestsInfrastructureError(original: Throwable) : TestsError(original, TestsExceptionType.INFRASTRUCTURE_ERROR)
class TestsCompiletimeError(original: Throwable) : TestsError(original, TestsExceptionType.COMPILETIME_ERROR)
class TestsRuntimeError(original: Throwable) : TestsError(original, TestsExceptionType.RUNTIME_ERROR)

private enum class ExceptionType {
    ANALYZING_EXPRESSION,
    UNKNOWN
}

class TestExceptionsComparator(wholeFile: File) {
    companion object {
        private const val EXCEPTIONS_FILE_PREFIX = "exceptions"

        private val exceptionMessagePatterns = mapOf(
            ExceptionType.ANALYZING_EXPRESSION to
                    Pattern.compile("""Exception while analyzing expression at \((?<lineNumber>\d+),(?<symbolNumber>\d+)\) in /(?<filename>.*?)$""")
        )
        private val ls = System.lineSeparator()
    }

    private val filePathPrefix = "${wholeFile.parent}/${wholeFile.nameWithoutExtension}.$EXCEPTIONS_FILE_PREFIX"

    private fun analyze(e: Throwable): Matcher? {
        for ((_, pattern) in exceptionMessagePatterns) {
            if (e.message == null) continue
            val matches = pattern.matcher(e.message)
            if (matches.find()) return matches
        }

        return null
    }

    private fun getExceptionInfo(e: TestsError) = when (e) {
        is TestsRuntimeError ->
            (e.original.cause ?: e.original).run {
                toString() + stackTrace[0]?.let { ls + it }
            }
        is TestsCompilerError, is TestsCompiletimeError, is TestsInfrastructureError -> (e.original.cause ?: e.original).toString()
    }

    private fun validateExistingExceptionFiles(e: TestsError?) {
        val postfixesOfFilesToCheck = TestsExceptionType.values().toMutableSet().filter { it != e?.type }

        postfixesOfFilesToCheck.forEach {
            if (File("$filePathPrefix.${it.postfix}.txt").exists())
                Assert.fail("No $it, but file $filePathPrefix.${it.postfix}.txt exists.")
        }
    }

    fun run(exception: TestsExceptionType, runnable: () -> Unit) {
        try {
            runnable()
        } catch (e: TestsError) {
            if (e.type != exception) throw e

            val exceptionsFile = File("$filePathPrefix.${e.type.postfix}.txt")

            try {
                KotlinTestUtils.assertEqualsToFile(exceptionsFile, getExceptionInfo(e))
            } catch (t: AssertionError) {
                e.original.printStackTrace()
                throw t
            }

            e.original.printStackTrace()
            validateExistingExceptionFiles(e)
            return
        }
        throw IllegalArgumentException()
    }
}
