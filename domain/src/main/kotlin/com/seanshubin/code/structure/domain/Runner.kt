package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path
import java.nio.file.Paths

class Runner(
    private val inputFile: Path,
    private val files: FilesContract,
    private val reportGenerator: ReportGenerator,
    private val timeTaken: (Long) -> Unit
) : Runnable {
    override fun run() {
        val startTime = System.currentTimeMillis()
        runMeInsideTimer()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        timeTaken(duration)
    }

    private fun runMeInsideTimer() {
        val inputLines = files.readAllLines(inputFile)
        val detail = DetailBuilder.fromLines(inputLines)
        reportGenerator.generateReports(detail)
    }
}
