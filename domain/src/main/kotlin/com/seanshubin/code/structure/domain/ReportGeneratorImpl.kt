package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.domain.NameComposer.dotFileName
import com.seanshubin.code.structure.domain.NameComposer.svgFileName
import java.nio.file.Path

class ReportGeneratorImpl(
    private val htmlReportFormat: ReportFormat,
    private val dotReportFormat: ReportFormat,
    private val tableOfContentsFormat:ReportFormat,
    private val listFormat:ReportFormat,
    private val entryPointFormat:ReportFormat,
    private val cycleReportFormat:ReportFormat,
    private val files: FilesContract,
    private val svgGenerator: SvgGenerator,
    private val reportDir: Path,
    private val style: String
) : ReportGenerator {
    override fun generateReports(detail: Detail) {
        val allDetails = detail.thisAndFlattenedChildren()
        val detailsWithChildren = allDetails.filter { it.children.isNotEmpty() }
        val dotReports = detailsWithChildren.flatMap { dotReportFormat.generateReports(reportDir, it, style) }
        files.createDirectories(reportDir)
        writeResource(reportDir, "dependencies.css")
        writeResource(reportDir, "reset.css")
        val writeReport = writeReportFunction(reportDir)
        dotReports.forEach(writeReport)
        detailsWithChildren.forEach {
            val dotFileName = it.dotFileName()
            val svgFileName = it.svgFileName()
            svgGenerator.generate(reportDir, dotFileName, svgFileName)
        }
        val htmlReports = allDetails.flatMap { htmlReportFormat.generateReports(reportDir, it, style) }
        htmlReports.forEach(writeReport)
        generateReport(tableOfContentsFormat, detail)
        generateReport(listFormat, detail)
        generateReport(entryPointFormat, detail)
        generateReport(cycleReportFormat, detail)
    }

    private fun generateReport(reportFormat:ReportFormat, detail:Detail){
        val reports = reportFormat.generateReports(reportDir, detail, style)
        reports.forEach(writeReportFunction(reportDir))
    }

    private fun writeReportFunction(reportDir: Path): (Report) -> Unit = { report ->
        val name = report.name
        val path = reportDir.resolve(name)
        val lines = report.lines
        files.write(path, lines)
    }

    private fun writeResource(dir: Path, name: String) {
        val resourceDir = "/com/seanshubin/code/structure/domain"
        val destinationPath = dir.resolve(name)
        val resourceName = "$resourceDir/$name"
        javaClass.getResourceAsStream(resourceName).use { inputStream ->
            if (inputStream == null) {
                throw RuntimeException("Resource '$resourceName' not found")
            }
            files.newOutputStream(destinationPath).use { outputStream ->
                inputStream.sendTo(outputStream)
            }
        }
    }
}
