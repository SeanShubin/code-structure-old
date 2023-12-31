package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportLink
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceLink
import com.seanshubin.code.structure.domain.NameComposer.localCycleDisplayName
import com.seanshubin.code.structure.domain.NameComposer.localCycleLink
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class CycleReportFormat(
    private val sourcePrefix: String,
) : ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail): List<Report> {
        val name = "cycles"
        val title = "Cycles"
        val tableOfContentsLink = HtmlElement.a("table of contents", "index.html")
        val tables = createFieldsets(detail)
        val html = GlobalHtml.standardHtml(title, tableOfContentsLink, tables)
        return listOf(Report(name, html.toLines(), Report.Type.HTML))
    }

    private fun createFieldsets(detail: Detail): HtmlElement {
        val cycles = detail.findAllCycles()
        val elements = cycles.map { createFieldset(it) }
        return HtmlElement.div(elements)
    }

    private fun createFieldset(cycle: List<Detail>): HtmlElement {
        val caption = "Cycle (${cycle.size})"
        val table = createTable(cycle)
        return HtmlElement.fieldset(caption, table)
    }

    private fun createTable(cycle: List<Detail>): HtmlElement {
        val headerRows = listOf(createHeaderRow())
        val bodyRows = cycle.map(::createRow)
        return HtmlElement.table(headerRows, bodyRows)
    }

    private fun createRow(detail: Detail): HtmlElement {
        val tdList = listOf(
            HtmlElement.td(reportAnchor(detail)),
            HtmlElement.td(localCycleAnchor(detail)),
            HtmlElement.td(detail.depth.toString()),
            HtmlElement.td(detail.transitive.toString()),
            HtmlElement.td(detail.dependsOn.size.toString()),
            HtmlElement.td(detail.dependedOnBy.size.toString()),
            HtmlElement.td(sourceAnchor(detail))
        )
        val tr = HtmlElement.tr(tdList)
        return tr
    }

    private fun reportAnchor(detail: Detail): HtmlElement =
        HtmlElement.a(detail.htmlAnchorReportDisplayName(), detail.htmlAnchorReportLink())

    private fun localCycleAnchor(detail: Detail): HtmlElement =
        HtmlElement.a(detail.localCycleDisplayName(), detail.localCycleLink())

    private fun sourceAnchor(detail: Detail): HtmlElement =
        HtmlElement.a(
            detail.htmlAnchorSourceDisplayName(),
            detail.htmlAnchorSourceLink(sourcePrefix) ?: "source missing"
        )

    private fun createHeaderRow(): HtmlElement {
        val headerNames = listOf(
            "report",
            "local cycle",
            "depth",
            "transitive",
            "depends on",
            "depended on by",
            "source"
        )
        val cells = headerNames.map(HtmlElement::th)
        val row = HtmlElement.tr(cells)
        return row
    }
}
