package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.localCycleDotReportBaseName
import java.nio.file.Path

class LocalCycleDotReportFormat : ReportFormat {
    override fun generateReports(reportDir: Path, detail: Detail): List<Report> {
        val cycles = detail.findAllCycles()
        return cycles.flatMap(::generateReports)
    }

    private fun generateReports(cycle: List<Detail>): List<Report> {
        return cycle.map(::generateReport)
    }

    private fun generateReport(detail: Detail): Report {
        val name = detail.localCycleDotReportBaseName()
        val lines = detail.localCycleDotModel().toDotLines()
        return Report(name, lines, Report.Type.DOT)
    }
}