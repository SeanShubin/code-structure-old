package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.localCycleDotReportBaseName
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class LocalCycleHtmlReportFormat(
    private val loadSvgLines: (Path, String) -> List<String>
):ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail): List<Report> {
        val cycles = detail.findAllCycles()
        return cycles.flatMap{generateReports(reportDir, it)}
    }
    private fun generateReports(reportDir:Path, cycle:List<Detail>):List<Report>{
        return cycle.map{generateReport(reportDir, it)}
    }
    private fun generateReport(reportDir:Path, detail:Detail):Report {
        val name = detail.localCycleDotReportBaseName()
        val tableOfContentsLink = HtmlElement.p(HtmlElement.a("table of contents", "index.html"))
        val svgLines = loadSvgLines(reportDir, detail.localCycleDotReportBaseName() )
        val svgElement = HtmlElement.Text(svgLines)
        val html = GlobalHtml.standardHtml(name, tableOfContentsLink, svgElement)
        val lines = html.toLines()
        return Report(name, lines, Report.Type.HTML)
    }
}