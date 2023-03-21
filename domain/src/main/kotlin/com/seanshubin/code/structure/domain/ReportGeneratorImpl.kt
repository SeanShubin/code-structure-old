package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class ReportGeneratorImpl(
    private val reportFormat: ReportFormat,
    private val files: FilesContract,
    private val svgGenerator: SvgGenerator,
) : ReportGenerator {
    override fun generateReports(detail: Detail, reportDir: Path, style: String) {
        val reports = reportFormat.generateReports(detail, style)
        files.createDirectories(reportDir)
        fun writeReport(report: Report) {
            val baseName = report.name
            val dotName = "$baseName.txt"
            val svgName = "$baseName.svg"
            val dotFile = reportDir.resolve(dotName)
            val svgFile = reportDir.resolve(svgName)
            val reportLines = report.lines
            files.write(dotFile, reportLines)
            svgGenerator.generate(reportDir, dotName, svgName)
        }
        reports.forEach(::writeReport)
    }
}
