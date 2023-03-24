package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Detail.Companion.depthDescendingNameAscending
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchor
import com.seanshubin.code.structure.domain.NameComposer.htmlDisplay
import com.seanshubin.code.structure.domain.NameComposer.htmlFileName
import com.seanshubin.code.structure.domain.NameComposer.svgFileName
import java.nio.file.Path

class HtmlReportFormat(
    private val loadSvgLines:(Path, Detail)->List<String>
) : ReportFormat {
    override fun report(reportDir:Path, detail: Detail, style: String): Report? {
        val fileName = detail.htmlFileName()
        val lines = header(detail) + body(reportDir, detail) + footer()
        return Report(fileName, lines)
    }

    private fun header(detail: Detail): List<String> {
        val title = detail.htmlDisplay()
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
               <meta charset="UTF-8">
               <title>$title</title>
               <link rel="stylesheet" href="reset.css">
               <link rel="stylesheet" href="dependencies.css">
            </head>
            <body>
            <h1>$title</h1>
        """.trimIndent().split("\n")
    }

    private fun body(reportDir:Path, detail: Detail): List<String> =
        parentLink(detail) +
                graph(reportDir,detail) +
                children(detail) +
                dependsOn(detail) +
                cycleDependsOn(detail) +
                cycle(detail) +
                cycleDependedOnBy(detail) +
                dependedOnBy(detail) +
                reasons(detail)


    private fun parentLink(detail: Detail): List<String> {
        if (detail.name.parts.isEmpty()) return emptyList()
        val parentNameParts = detail.name.parts.dropLast(1)
        val parentAnchor = parentNameParts.htmlAnchor()
        return listOf("""<h2>$parentAnchor</h2>""")
    }

    private fun graph(reportDir:Path, detail: Detail): List<String> {
        if (detail.children.isEmpty()) return emptyList()
        val svgLines = loadSvgLines(reportDir, detail)
        return svgLines
    }

    private fun reasons(detail: Detail): List<String> {
        val relations = detail.relations().all
        val lines = relations.flatMap(::reasonsForRelations)
        val caption = "relations (${relations.size})"
        return wrapInFieldset(caption, lines)
    }

    private fun reasonsForRelations(relationWithReasons: RelationWithReasons): List<String> {
        val caption = relationWithReasons.relation.htmlDisplay()
        return relationTable(caption, relationWithReasons.reasons)
    }

    private fun children(detail: Detail): List<String> {
        return detailTable("children", detail.children)
    }

    private fun cycle(detail: Detail): List<String> {
        val inCycle = if (detail.cycleExcludingThis.isEmpty()) emptyList() else detail.cycleIncludingThis
        return detailTable("cycle", inCycle)
    }

    private fun cycleDependsOn(detail: Detail): List<String> {
        val inCycle = if (detail.cycleExcludingThis.isEmpty()) emptyList() else detail.thisOrCycleDependsOn
        return detailTable("cycle depends on", inCycle)
    }

    private fun cycleDependedOnBy(detail: Detail): List<String> {
        val inCycle = if (detail.cycleExcludingThis.isEmpty()) emptyList() else detail.thisOrCycleDependedOnBy
        return detailTable("cycle depended on by", inCycle)
    }

    private fun dependsOn(detail: Detail): List<String> {
        return detailTable("depends on", detail.dependsOn)
    }

    private fun dependedOnBy(detail: Detail): List<String> {
        return detailTable("depended on by", detail.dependedOnBy)
    }

    private fun wrapInFieldset(caption: String, lines: List<String>): List<String> {
        return listOf(
            "<fieldset>",
            "<legend>$caption</legend>"
        ) + lines.map { "  $it" } + listOf("</fieldset>")
    }

    private fun detailTable(caption: String, rows: List<Detail>): List<String> {
        if (rows.isEmpty()) return emptyList()
        val header = """
            <fieldset>
                <legend>$caption (${rows.size})</legend>
                <table>
                    <thead>
                    <tr>
                        <th>depth</th>
                        <th>transitive</th>
                        <th>depends on</th>
                        <th>depended on by</th>
                        <th>children</th>
                        <th>link</th>
                    </tr>
                    </thead>
                    <tbody>
        """.trimIndent().split("\n")
        val footer = """
                    </tbody>
                </table>
            </fieldset>
        """.trimIndent().split("\n")
        val rowLines = rows.sortedWith(depthDescendingNameAscending).flatMap(::detailRow)
        return header + rowLines + footer
    }

    private fun detailRow(detail: Detail): List<String> {
        return listOf(
            "<tr>",
            td(detail.depth),
            td(detail.transitive),
            td(detail.dependsOn.size),
            td(detail.dependedOnBy.size),
            td(detail.children.size),
            td(detail.htmlAnchor()),
            "</tr>"
        )
    }

    private fun relationRow(relation: Relation): List<String> {
        return listOf(
            "<tr>",
            td(relation.first.htmlAnchor()),
            td("->"),
            td(relation.second.htmlAnchor()),
            "</tr>"
        )
    }

    private fun relationTable(caption: String, rows: List<Relation>): List<String> {
        if (rows.isEmpty()) return emptyList()
        val header = """
            <fieldset>
                <legend>$caption (${rows.size})</legend>
                <table>
                    <tbody>
        """.trimIndent().split("\n")
        val footer = """
                    </tbody>
                </table>
            </fieldset>
        """.trimIndent().split("\n")
        val rowLines = rows.sorted().flatMap(::relationRow)
        return header + rowLines + footer
    }

    private fun td(x: Int): String = "<td>$x</td>"
    private fun td(s: String): String = "<td>$s</td>"

    private fun footer(): List<String> {
        return """
            </body>
            </html>
        """.trimIndent().split("\n")
    }
}
