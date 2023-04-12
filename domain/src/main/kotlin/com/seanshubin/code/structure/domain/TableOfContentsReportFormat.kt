package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class TableOfContentsReportFormat : ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail, style: String): List<Report> {
        val name = "index"
        val title = "Dependency Report"
        val header = HtmlElement.h1(title)
        val graphs = HtmlElement.p(HtmlElement.a("Graph", "dependencies.html"))
        val list = HtmlElement.p(HtmlElement.a("List", "list.html"))
        val entryPoints = HtmlElement.p(HtmlElement.a("Entry Points", "entry-points.html"))
        val cycles = HtmlElement.p(HtmlElement.a("Cycles", "cycles.html"))
        val html = GlobalHtml.standardHtml(title, header, graphs, list, entryPoints, cycles)
        return listOf(Report(name, html.toLines(), Report.Type.HTML))
    }
}
