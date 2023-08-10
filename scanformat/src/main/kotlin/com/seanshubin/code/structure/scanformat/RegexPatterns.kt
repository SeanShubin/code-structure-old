package com.seanshubin.code.structure.scanformat

object RegexPatterns {
    private val moduleCharacter = """[\w\.]"""
    val module = Regex("""defmodule\s+($moduleCharacter+)\s+do""")
    val dependencyLine = Regex("""^\s*(?:use|alias|import|require)\s+(.*)\s*$""", RegexOption.MULTILINE)
}
