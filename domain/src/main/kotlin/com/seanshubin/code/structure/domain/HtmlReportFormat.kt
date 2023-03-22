package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Detail.Companion.depthDescendingNameAscending

class HtmlReportFormat : ReportFormat {
    override fun report(detail: Detail, style: String): Report? {
        val baseName = baseName(detail)
        val lines = header(detail) + body(detail) + footer()
        return Report(baseName, "html", lines, isGraphSource = false)
    }

    private fun moduleName(parts:List<String>):String =
        if(parts.isEmpty()) "--root--"
        else parts.joinToString(".")
    private fun moduleName(detail:Detail):String = moduleName(detail.name.parts)

    private fun baseName(parts:List<String>):String = (listOf("dependencies") + parts).joinToString("-")
    private fun baseName(detail:Detail):String = baseName(detail.name.parts)

    private fun header(detail:Detail): List<String> {
        val title = moduleName(detail)
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

    private fun body(detail:Detail): List<String> =
        parentLink(detail) +
                children(detail) +
                dependsOn(detail) +
                cycleDependsOn(detail) +
                cycle(detail) +
                cycleDependedOnBy(detail) +
                dependedOnBy(detail) +
                graph(detail)

    private fun parentLink(detail:Detail):List<String> {
        if(detail.name.parts.isEmpty()) return emptyList()
        val parentNameParts = detail.name.parts.dropLast(1)
        val parentName = moduleName(parentNameParts)
        val parentLink = baseName(parentNameParts)
        val parentAnchor = """<a href="$parentLink.html">$parentName</a>"""
        return listOf("""<h2>$parentAnchor</h2>""")
    }

    private fun graph(detail: Detail): List<String> {
        if (detail.children.isEmpty()) return emptyList()
        val baseName = baseName(detail)
        return """
            <object type="image/svg+xml" data="$baseName.svg">
            </object>
        """.trimMargin().split("\n")
    }

    private fun children(detail: Detail): List<String> {
        return table("children", detail.children)
    }

    private fun cycle(detail: Detail): List<String> {
        val inCycle = if(detail.cycleExcludingThis.isEmpty()) emptyList() else detail.cycleIncludingThis
        return table("cycle", inCycle)
    }

    private fun cycleDependsOn(detail: Detail): List<String> {
        val inCycle = if(detail.cycleExcludingThis.isEmpty()) emptyList() else detail.thisOrCycleDependsOn
        return table("cycle depends on", inCycle)
    }

    private fun cycleDependedOnBy(detail: Detail): List<String> {
        val inCycle = if(detail.cycleExcludingThis.isEmpty()) emptyList() else detail.thisOrCycleDependedOnBy
        return table("cycle depended on by", inCycle)
    }

    private fun dependsOn(detail: Detail): List<String> {
        return table("depends on", detail.dependsOn)
    }

    private fun dependedOnBy(detail: Detail): List<String> {
        return table("depended on by", detail.dependedOnBy)
    }

    private fun table(caption: String, rows: List<Detail>): List<String> {
        if(rows.isEmpty()) return emptyList()
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
            td(generateLink(detail)),
            "</tr>"
        )
    }

    private fun generateLink(detail: Detail): String {
        val linkBaseName = baseName(detail)
        val linkName = "$linkBaseName.html"
        val name = moduleName(detail)
        return """<a href="$linkName">$name</a>"""
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
