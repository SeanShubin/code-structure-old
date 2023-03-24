package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.process.ProcessRunner
import com.seanshubin.code.structure.process.SystemProcessRunner
import java.nio.file.Path

class Dependencies(val args: Array<String>) {
    private val files: FilesContract = FilesDelegate
    private val exit: (Int) -> Nothing = { code ->
        System.exit(code)
        throw RuntimeException("system exited with code $code")
    }
    private val emitLine: (String) -> Unit = ::println
    private val processRunner: ProcessRunner = SystemProcessRunner()
    private val svgGenerator: SvgGenerator = SvgGeneratorImpl(processRunner)
    private val notifications: Notifications = LineEmittingNotifications(emitLine)
    private val errorHandler: ErrorHandler = ErrorHandlerImpl(
        notifications::error,
        exit
    )
    private val simpleReportStyle: ReportStyle = SimpleReportStyle()
    private val tableReportStyle: ReportStyle = TableReportStyle()
    private val reportStyleMap: Map<String, ReportStyle> = mapOf(
        "simple" to simpleReportStyle,
        "table" to tableReportStyle
    )
    private val dotReportFormat: ReportFormat = DotReportFormat(reportStyleMap)
    private val loadSvgLines:(Path, Detail)->List<String> = SvgLoader(files)
    private val htmlReportFormat: ReportFormat = HtmlReportFormat(loadSvgLines)
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(
        htmlReportFormat,
        dotReportFormat,
        files,
        svgGenerator)
    val runner: Runnable = Runner(
        args,
        files,
        reportGenerator,
        notifications::timeTaken,
        errorHandler::error
    )
}
