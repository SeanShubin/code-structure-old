package com.seanshubin.code.structure.domain

object Format {
    private val namePattern = """\w+(?:\.\w+)*"""
    private val nameSourceRegex = Regex("""($namePattern)\s+(.*)""")
    private val relationRegex = Regex("""($namePattern)\s*->\s*($namePattern)""")
    private fun parseNameSource(line: String): NameSource? {
        val matchResult = nameSourceRegex.matchEntire(line) ?: return null
        val name = Name(matchResult.groupValues[1].split("."))
        val source = matchResult.groupValues[2]
        return NameSource(name, source)
    }

    private fun parseRelation(line: String): Relation? {
        val matchResult = relationRegex.matchEntire(line) ?: return null
        val first = Name(matchResult.groupValues[1].split('.'))
        val second = Name(matchResult.groupValues[2].split('.'))
        return Relation(first, second)
    }

    fun parseInputLines(lines: List<String>): Pair<List<NameSource>, List<Relation>> {
        val nameSources = mutableListOf<NameSource>()
        val relations = mutableListOf<Relation>()
        lines.forEachIndexed { index, line ->
                val relation = parseRelation(line)
                if (relation == null) {
                    val nameSource = parseNameSource(line)
                    if (nameSource == null) {
                        throw RuntimeException("Input line '$line' at index $index did not match '$nameSourceRegex' or '$relationRegex'")
                    } else {
                        nameSources.add(nameSource)
                    }
                } else {
                    relations.add(relation)
                }
        }
        return Pair(nameSources, relations)
    }
}
