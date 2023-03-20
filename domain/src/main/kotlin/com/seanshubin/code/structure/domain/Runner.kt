package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Paths

class Runner(
    val args: Array<String>,
    val files: FilesContract,
    val exit: (Int) -> Nothing,
    val reportFormat:ReportFormat,
    val svgGenerator: SvgGenerator,
    val timeTaken:(Long) -> Unit,
    val error:(String)->Nothing
) : Runnable {
    override fun run() {
        val startTime = System.currentTimeMillis()
        runMeInsideTimer()
        val endTime = System.currentTimeMillis()
        val duration  = endTime - startTime
        timeTaken(duration)
    }

    private fun runMeInsideTimer(){
        val inputFileName = args.getOrNull(0) ?: error("first parameter must be input file")
        val reportDirName = args.getOrNull(1) ?: error("second parameter must be report directory")
        val reportDir = Paths.get(reportDirName)
        val inputFile = Paths.get(inputFileName)
        val inputLines = files.readAllLines(inputFile)
        val detail = DetailBuilder.fromLines(inputLines)
        val reports = reportFormat.generateReports(detail)
        files.createDirectories(reportDir)
        fun writeReport(report: Report) {
            val baseName = report.name
            val dotName = "$baseName.txt"
            val svgName = "$baseName.svg"
            val dotFile = reportDir.resolve(dotName)
            val svgFile = reportDir.resolve(svgName)
            val reportLines = report.dotLines
            files.write(dotFile, reportLines)
            svgGenerator.generate(reportDir, dotName, svgName)
        }
        reports.forEach(::writeReport)
    }
}
