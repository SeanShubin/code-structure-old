package com.seanshubin.code.structure.process

import java.nio.file.Path

data class ProcessInput(val command: List<String>, val directory: Path) {
    fun toLines(): List<String> = listOf("ProcessInput") + compose().map { "  $it" }
    private fun compose(): List<String> = composeDirectory() + composeCommand()
    private fun composeDirectory(): List<String> = listOf("directory = $directory")
    private fun composeCommand(): List<String> = listOf("command") + listOf(command.joinToString(" ")).map { "  $it" }
}
