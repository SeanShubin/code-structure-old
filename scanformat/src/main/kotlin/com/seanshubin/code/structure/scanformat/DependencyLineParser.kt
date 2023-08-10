package com.seanshubin.code.structure.scanformat

interface DependencyLineParser {
    fun parseDependencies(line: String): List<String>?
}
