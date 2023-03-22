package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class ReportGeneratorImpl(
    private val reportFormats: List<ReportFormat>,
    private val files: FilesContract,
    private val svgGenerator: SvgGenerator,
) : ReportGenerator {
    override fun generateReports(detail: Detail, reportDir: Path, style: String) {
        val allDetails = detail.thisAndFlattenedChildren()
        val reports = allDetails.flatMap { currentDetail ->
            reportFormats.mapNotNull{ reportFormat ->
                reportFormat.report(currentDetail, style)
            }
        }
        files.createDirectories(reportDir)
        writeResource(reportDir, "dependencies.css")
        writeResource(reportDir, "reset.css")
        val writeReport = writeReportFunction(reportDir)
        reports.forEach(writeReport)
    }

    private fun writeReportFunction(reportDir:Path):(Report)->Unit = { report ->
        val baseName = report.baseName
        val extension = report.extension
        val name = "$baseName.$extension"
        val path = reportDir.resolve(name)
        val lines = report.lines
        files.write(path, lines)
        if(report.isGraphSource){
            val svgName = "$baseName.svg"
            svgGenerator.generate(reportDir, name, svgName)
        }
    }

    private fun writeResource(dir:Path, name:String){
        val resourceDir = "/com/seanshubin/code/structure/domain"
        val destinationPath = dir.resolve(name)
        val resourceName = "$resourceDir/$name"
        javaClass.getResourceAsStream(resourceName).use {
            inputStream ->
            if(inputStream == null){
                throw RuntimeException("Resource '$resourceName' not found")
            }
            files.newOutputStream(destinationPath).use {
                outputStream ->
                inputStream.sendTo(outputStream)
            }
        }
    }
}
