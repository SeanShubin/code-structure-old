package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.process.ProcessRunner
import com.seanshubin.code.structure.process.SystemProcessRunner

class Dependencies(val args: Array<String>) {
    val files: FilesContract = FilesDelegate
    val exit: (Int) -> Nothing = { code ->
        System.exit(code)
        throw RuntimeException("system exited with code $code")
    }
    val emitLine: (String) -> Unit = ::println
    val processRunner: ProcessRunner = SystemProcessRunner()
    val svgGenerator: SvgGenerator = SvgGeneratorImpl(processRunner)
    val notifications: Notifications = LineEmittingNotifications(emitLine)
    val errorHandler: ErrorHandler = ErrorHandlerImpl(
        notifications::error,
        exit
    )
    val simpleReportStyle: ReportStyle = SimpleReportStyle()
    val tableReportStyle:ReportStyle = TableReportStyle()
    val reportStyleMap:Map<String, ReportStyle> = mapOf(
        "simple" to simpleReportStyle,
        "table" to tableReportStyle
    )
    val reportFormat: ReportFormat = DotReportFormat(reportStyleMap)
    val runner: Runnable = Runner(
        args,
        files,
        exit,
        reportFormat,
        svgGenerator,
        notifications::timeTaken,
        errorHandler::error
    )
}