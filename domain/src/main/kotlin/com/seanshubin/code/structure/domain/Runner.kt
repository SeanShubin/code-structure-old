package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.SystemContract
import java.nio.file.Path

class Runner(
    private val inputFile: Path,
    private val files: FilesContract,
    private val reportGenerator: ReportGenerator,
    private val system: SystemContract,
    private val timeTaken: (Long) -> Unit
) : Runnable {
    override fun run() {
        val startTime = system.currentTimeMillis()
        runMeInsideTimer()
        val endTime = system.currentTimeMillis()
        val duration = endTime - startTime
        timeTaken(duration)
    }

    private fun runMeInsideTimer() {
        val inputLines = files.readAllLines(inputFile)
        val detail = DetailBuilder.fromLines(inputLines)
        reportGenerator.generateReports(detail)
    }
}
