package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Paths

class Runner(
    val args: Array<String>,
    val files: FilesContract,
    val exit:(Int)->Nothing,
    val emitLine:(String)->Unit,
    val svgGenerator:SvgGenerator
) : Runnable {
    override fun run() {
        val inputFileName = args.getOrNull(0) ?: throwError("first parameter must be input file")
        val reportDirName = args.getOrNull(1) ?: throwError("second parameter must be report directory")
        val reportDir = Paths.get(reportDirName)
        val inputFile = Paths.get(inputFileName)
        val inputLines = files.readAllLines(inputFile)
        val lookup = Lookup.fromLines(inputLines)
        val reports = lookup.generateReports()
        fun writeReport(report:Report){
            val baseName = report.baseName
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

    private fun throwError(message:String):Nothing {
        emitLine(message)
        exit(1)
    }
}
