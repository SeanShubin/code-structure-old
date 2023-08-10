package com.seanshubin.code.structure.scanformat

class DependencyLineParserImpl : DependencyLineParser {
    override fun parseDependencies(line: String): List<String>? {
        parsers.forEach {
            val parsed = it(line)
            if (parsed != null) return parsed
        }
        return null
    }

    private val simple = Regex("""([\w.]+)""")
    private val modifier = Regex("""([\w.]+), \w+: \w*""")
    private val multiple = Regex("""([\w.]+)\.\{(\w+(?:,\s*\w+)*)}""")
    private val nameSeparator = Regex(""",\s*""")
    private val parsers: List<(String) -> List<String>?> = listOf(
        ::parseSimple,
        ::parseModifier,
        ::parseMultiple
    )

    private fun parseSimple(line: String): List<String>? {
        val matchResult = simple.matchEntire(line)
        return if (matchResult == null) {
            null
        } else {
            listOf(matchResult.groupValues[1])
        }
    }

    private fun parseModifier(line: String): List<String>? {
        val matchResult = modifier.matchEntire(line)
        return if (matchResult == null) {
            null
        } else {
            listOf(matchResult.groupValues[1])
        }
    }

    private fun parseMultiple(line: String): List<String>? {
        val matchResult = multiple.matchEntire(line)
        return if (matchResult == null) {
            null
        } else {
            val base = matchResult.groupValues[1]
            val remain = splitNames(matchResult.groupValues[2])
            expandList(base, remain)
        }
    }

    private fun splitNames(line: String): List<String> = line.split(nameSeparator)
    private fun expandList(base: String, remain: List<String>): List<String> =
        remain.map {
            "$base.$it"
        }
}
