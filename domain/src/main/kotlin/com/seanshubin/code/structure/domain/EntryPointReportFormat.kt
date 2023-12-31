package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorReportLink
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceDisplayName
import com.seanshubin.code.structure.domain.NameComposer.htmlAnchorSourceLink
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class EntryPointReportFormat(
    private val sourcePrefix: String,
) : ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail): List<Report> {
        val name = "entry-points"
        val title = "Entry points or dead code"
        val tableOfContentsLink = HtmlElement.a("table of contents", "index.html")
        val table = createTable(detail)
        val html = GlobalHtml.standardHtml(title, tableOfContentsLink, table)
        return listOf(Report(name, html.toLines(), Report.Type.HTML))
    }

    private fun createTable(detail: Detail): HtmlElement {
        val list = detail.entryPoints()
        val headerRow = createHeaderRow()
        val rows = list.map(::createRow)
        val table = HtmlElement.table(listOf(headerRow), rows)
        val caption = "Entry Points (${list.size})"
        val fieldset = HtmlElement.fieldset(caption, table)
        return fieldset
    }

    private fun createRow(detail: Detail): HtmlElement {
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

    fun reportAnchor(detail: Detail): HtmlElement =
        HtmlElement.a(detail.htmlAnchorReportDisplayName(), detail.htmlAnchorReportLink())

    fun sourceAnchor(detail: Detail): HtmlElement =
        HtmlElement.a(
            detail.htmlAnchorSourceDisplayName(),
            detail.htmlAnchorSourceLink(sourcePrefix) ?: "source missing"
        )

    private fun createHeaderRow(): HtmlElement {
        val headerNames = listOf("report", "depth", "transitive", "depends on", "depended on by", "source")
        val cells = headerNames.map(HtmlElement::th)
        val row = HtmlElement.tr(cells)
        return row
    }
}
