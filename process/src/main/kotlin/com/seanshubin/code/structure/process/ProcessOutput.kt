package com.seanshubin.code.structure.process

data class ProcessOutput(
    val exitCode: Int,
    val outputLines: List<String>,
    val errorLines: List<String>
) {
    fun toLines(): List<String> = listOf("ProcessOutput") + compose().map { "  $it" }

    private fun compose(): List<String> = composeExitCode() + composeOutputLines() + composeErrorLines()
    private fun composeExitCode(): List<String> = listOf("exitCode = $exitCode")
    private fun composeOutputLines(): List<String> =
        listOf("outputLines (${outputLines.size})") + outputLines.map { "  $it" }

    private fun composeErrorLines(): List<String> =
        listOf("errorLines (${errorLines.size})") + errorLines.map { "  $it" }
}
