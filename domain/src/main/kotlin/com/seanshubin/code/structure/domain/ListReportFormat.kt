package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportLink
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceLink
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class ListReportFormat(
    private val sourcePrefix: String,
) : ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail, style: String): List<Report> {
        val name = "list.html"
        val title = "All Dependencies"
        val tableOfContentsLink = HtmlElement.a("table of contents", "index.html")
        val table = createTable(detail)
        val html = GlobalHtml.standardHtml(title, tableOfContentsLink, table)
        return listOf(Report(name, html.toLines()))
    }

    private fun createTable(detail:Detail):HtmlElement {
        val list = detail.allWithSource()
        val headerRow = createHeaderRow()
        val rows = list.map(::createRow)
        val table = HtmlElement.table(listOf(headerRow), rows)
        val caption = "All Dependencies (${list.size})"
        val fieldset = HtmlElement.fieldset(caption, table)
        return fieldset
    }

    private fun createRow(detail:Detail):HtmlElement {
        val tdList = listOf(
            HtmlElement.td(reportAnchor(detail)),
            HtmlElement.td(detail.depth.toString()),
            HtmlElement.td(detail.transitive.toString()),
            HtmlElement.td(detail.dependsOn.size.toString()),
            HtmlElement.td(detail.dependedOnBy.size.toString()),
            HtmlElement.td(sourceAnchor(detail))
        )
        val tr = HtmlElement.tr(tdList)
        return tr
    }

    fun reportAnchor(detail:Detail):HtmlElement =
        HtmlElement.a(detail.htmlAnchorReportDisplayName(), detail.htmlAnchorReportLink())
    fun sourceAnchor(detail:Detail):HtmlElement =
        HtmlElement.a(detail.htmlAnchorSourceDisplayName(), detail.htmlAnchorSourceLink(sourcePrefix) ?: "source missing")

    private fun createHeaderRow():HtmlElement {
        val headerNames = listOf("report","depth", "transitive", "depends on", "depended on by", "source")
        val cells = headerNames.map(HtmlElement::th)
        val row = HtmlElement.tr(cells)
        return row
    }
}
