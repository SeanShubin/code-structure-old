package com.seanshubin.code.structure.domain

class DotReportFormat(private val reportStyleMap: Map<String, ReportStyle>) : ReportFormat {
    override fun report(detail: Detail, style: String): Report? {
        if (detail.children.isEmpty()) return null
        val header = listOf(
            "digraph detangled {",
            "bgcolor=lightgray"
        )
        val body = reportBody(detail, style).indent("  ")
        val footer = listOf("}")
        val lines = header + body + footer
        val name = reportName(detail)
        return Report(name,".txt", lines, isGraphSource = true)
    }

    private fun reportBody(detail: Detail, style: String): List<String> {
        val singlesLines = reportSingles(detail.children, style)
        val relations = detail.relations()
        val relationsLines = reportRelations(relations.notInCycle)
        val cyclesLines = reportCycles(relations.cycles)
        return singlesLines + relationsLines + cyclesLines
    }

    private fun reportSingles(list: List<Detail>, style: String): List<String> =
        list.map { reportSingle(it, style) }

    private fun reportSingle(detail: Detail, style: String): String {
        val reportStyle = reportStyleMap[style] ?: styleNotFound(style)
        val urlAttribute = makeUrlAttribute(detail)
        val unquotedName = composeNodeName(detail.name)
        val labelAttribute = reportStyle.makeLabelAttribute(unquotedName, detail)
        val shapeAttribute = reportStyle.makeShapeAttribute(detail)
        val quotedName = composeQuotedNodeName(detail.name)
        val attributes = urlAttribute + labelAttribute + shapeAttribute
        return composeNameAndAttributes(quotedName, attributes)
    }

    private fun makeUrlAttribute(detail: Detail): List<Pair<String, String>> {
        if (detail.children.isEmpty()) return emptyList()
        val reportBaseName = reportBaseName(detail)
        val urlAttribute = "URL" to "$reportBaseName.svg".doubleQuote()
        val colorAttribute = "fontcolor" to "Blue"
        val attributes = listOf(urlAttribute, colorAttribute)
        return attributes
    }

    private fun reportBaseName(detail: Detail): String {
        val reportNameParts = listOf("dependencies") + detail.name.parts
        val reportBaseName = reportNameParts.joinToString("-")
        return reportBaseName
    }

    private fun composeNodeName(name: Name): String = name.parts.last()

    private fun composeQuotedNodeName(name: Name): String = composeNodeName(name).doubleQuote()

    private fun composeNameAndAttributes(name: String, attributes: List<Pair<String, String>>): String {
        if (attributes.isEmpty()) return name
        val attributesString = attributes.map { (first, second) ->
            "$first=$second"
        }.joinToString(" ", "[", "]")
        val line = listOf(name, attributesString).joinToString(" ")
        return line
    }

    private fun reportRelations(relations: List<Relation>): List<String> =
        relations.map(::composeRelationLine)

    private fun composeRelationLine(relation: Relation): String {
        val first = composeQuotedNodeName(relation.first)
        val second = composeQuotedNodeName(relation.second)
        return "$first -> $second"
    }

    private fun reportCycles(cycleAndRelationList: List<Pair<List<Name>, List<Relation>>>): List<String> =
        cycleAndRelationList.flatMapIndexed { index, (cycle, relations) ->
            reportCycleCluster(index, cycle, relations)
        }

    private fun reportCycleCluster(index: Int, cycle: List<Name>, relations: List<Relation>): List<String> {
        val header = listOf(
            "subgraph cluster_$index {",
            "  penwidth=2",
            "  pencolor=Red"
        )
        val body = relations.map(::composeRelationLine).indent("  ")
        val footer = listOf(
            "}"
        )
        val lines = header + body + footer
        return lines
    }

    private fun reportName(detail: Detail): String {
        val reportNameParts = listOf("dependencies") + detail.name.parts
        val reportName = reportNameParts.joinToString("-")
        return reportName
    }

    private fun String.doubleQuote(): String = "\"$this\""
    private fun String.indent(prefix: String): String = "$prefix$this"
    private fun List<String>.indent(prefix: String): List<String> = map { it.indent(prefix) }

    private fun styleNotFound(style: String): Nothing {
        val styleListString = reportStyleMap.keys.sorted().joinToString("', '", "'", "'")
        val message = "Style '$style' not found, expected one of $styleListString"
        throw RuntimeException(message)
    }
}
