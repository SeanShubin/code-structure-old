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
    private val localCycleDotReportFormat:ReportFormat,
    private val localCycleHtmlReportFormat:ReportFormat,
    private val files: FilesContract,
    private val svgGenerator: SvgGenerator,
    private val reportDir: Path,
    private val style: String
) : ReportGenerator {
    override fun generateReports(detail: Detail) {
        val allDetails = detail.thisAndFlattenedChildren()
        val detailsWithChildren = allDetails.filter { it.children.isNotEmpty() }
        files.createDirectories(reportDir)
        writeResource(reportDir, "dependencies.css")
        writeResource(reportDir, "reset.css")
        generateReports(dotReportFormat, detail)
        generateReports(localCycleDotReportFormat, detail)
        detailsWithChildren.forEach {
            val dotFileName = it.dotFileName()
            val svgFileName = it.svgFileName()
            svgGenerator.generate(reportDir, dotFileName, svgFileName)
        }
        generateReports(htmlReportFormat, detail)
        generateReports(tableOfContentsFormat, detail)
        generateReports(listFormat, detail)
        generateReports(entryPointFormat, detail)
        generateReports(cycleReportFormat, detail)
        generateReports(localCycleHtmlReportFormat, detail)
    }

    private fun generateReports(reportFormat:ReportFormat, detail:Detail){
        val reports = reportFormat.generateReports(reportDir, detail)
        reports.forEach{ report ->
            val path = report.type.resolvePath(reportDir, report.name)
            val lines = report.lines
            files.write(path, lines)
            if(report.type == Report.Type.DOT){
                generateSvg(report)
            }
        }
    }

    private fun generateSvg(report:Report){
        val dotFileName = "${report.name}.txt"
        val svgFileName = "${report.name}.svg"
        svgGenerator.generate(reportDir, dotFileName, svgFileName)
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
