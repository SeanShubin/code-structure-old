package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class ReportGeneratorImpl(
    private val reportFormat: ReportFormat,
    private val files: FilesContract,
    private val svgGenerator: SvgGenerator,
) : ReportGenerator {
    override fun generateReports(detail: Detail, reportDir: Path, style: String) {
        val allDetails = detail.thisAndFlattenedChildren()
        val reports = allDetails.mapNotNull {
            reportFormat.report(detail, style)
        }
        files.createDirectories(reportDir)
        writeResource(reportDir, "dependencies.css")
        writeResource(reportDir, "reset.css")
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
                var x = inputStream.read()
                while(x != -1){
                    outputStream.write(x)
                    x = inputStream.read()
                }
            }
        }
    }
}
