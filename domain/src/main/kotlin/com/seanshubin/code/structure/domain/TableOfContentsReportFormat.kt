package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class TableOfContentsReportFormat : ReportFormat {
    override fun report(reportDir: Path, detail: Detail, style: String): Report? {
        val name = "index.html"
        val title = "Dependency Report"
        val header = HtmlElement.h1(title)
        val graphs = HtmlElement.p(HtmlElement.a("Graph", "dependencies.html"))
        val list = HtmlElement.p(HtmlElement.a("List", "list.html"))
        val html = GlobalHtml.standardHtml(title, header, graphs, list)
        return Report(name, html.toLines())
    }
}
