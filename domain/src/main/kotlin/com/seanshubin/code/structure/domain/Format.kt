package com.seanshubin.code.structure.domain

object Format {
    private val namePattern = """\w+(?:\.\w+)*"""
    private val nameBinarySourceRegex = Regex("""([^ ]+)\s+([^ ]+)\s+([^ ]+)""")
    private val relationRegex = Regex("""($namePattern)\s*->\s*($namePattern)""")
    private fun parseNameSource(line: String): NameBinarySource? {
        val matchResult = nameBinarySourceRegex.matchEntire(line) ?: return null
        val name = Name(matchResult.groupValues[1].split("."))
        val binary = matchResult.groupValues[2]
        val sourceValue = matchResult.groupValues[3]
        val source = if(sourceValue == "null") null else sourceValue
        return NameBinarySource(name, binary, source)
    }

    private fun parseRelation(line: String): Relation? {
        val matchResult = relationRegex.matchEntire(line) ?: return null
        val first = Name(matchResult.groupValues[1].split('.'))
        val second = Name(matchResult.groupValues[2].split('.'))
        return Relation(first, second)
    }

    fun parseInputLines(lines: List<String>): Pair<List<NameBinarySource>, List<Relation>> {
        val nameSources = mutableListOf<NameBinarySource>()
        val relations = mutableListOf<Relation>()
        lines.forEachIndexed { index, line ->
                val relation = parseRelation(line)
                if (relation == null) {
                    val nameSource = parseNameSource(line)
                    if (nameSource == null) {
                        throw RuntimeException("Input line '$line' at index $index did not match '$nameBinarySourceRegex' or '$relationRegex'")
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
