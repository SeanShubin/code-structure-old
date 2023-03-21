package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Paths

class Runner(
    val args: Array<String>,
    val files: FilesContract,
    val reportGenerator: ReportGenerator,
    val timeTaken: (Long) -> Unit,
    val error: (String) -> Nothing
) : Runnable {
    override fun run() {
        val startTime = System.currentTimeMillis()
        runMeInsideTimer()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        timeTaken(duration)
    }

    private fun runMeInsideTimer() {
        val inputFileName = args.getOrNull(0) ?: error("first parameter must be input file")
        val reportDirName = args.getOrNull(1) ?: error("second parameter must be report directory")
        val reportStyleName = args.getOrNull(2) ?: error("third parameter must be a report style name")
        val reportDir = Paths.get(reportDirName)
        val inputFile = Paths.get(inputFileName)
        val inputLines = files.readAllLines(inputFile)
        val detail = DetailBuilder.fromLines(inputLines)
        reportGenerator.generateReports(detail, reportDir, reportStyleName)
    }
}
