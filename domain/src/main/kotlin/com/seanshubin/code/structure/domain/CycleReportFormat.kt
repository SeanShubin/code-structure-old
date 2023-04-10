package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportLink
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceLink
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class CycleReportFormat(
    private val sourcePrefix: String,
) : ReportFormat {
    override fun report(reportDir: Path, detail: Detail, style: String): Report? {
        val name = "cycles.html"
        val title = "Cycles"
        val tableOfContentsLink = HtmlElement.a("table of contents", "index.html")
        val tables = createFieldsets(detail)
        val html = GlobalHtml.standardHtml(title, tableOfContentsLink, tables)
        return Report(name, html.toLines())
    }

    private fun createFieldsets(detail:Detail):HtmlElement {
        val cycles = detail.findAllCycles()
        val elements = cycles.map { createFieldset(it) }
        return HtmlElement.div(elements)
    }

    private fun createFieldset(cycle:List<Detail>):HtmlElement{
        val caption = "Cycle (${cycle.size})"
        val table = createTable(cycle)
        return HtmlElement.fieldset(caption, table)
    }

    private fun createTable(cycle: List<Detail>):HtmlElement {
        val headerRows = listOf(createHeaderRow())
        val bodyRows = cycle.map(::createRow)
        return HtmlElement.table(headerRows, bodyRows)
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
