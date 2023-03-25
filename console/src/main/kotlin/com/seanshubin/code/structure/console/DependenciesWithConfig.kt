package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.process.ProcessRunner
import com.seanshubin.code.structure.process.SystemProcessRunner
import java.nio.file.Path

class DependenciesWithConfig(config: CodeStructureAppConfig) {
    private val files: FilesContract = FilesDelegate
    private val emitLine: (String) -> Unit = ::println
    private val processRunner: ProcessRunner = SystemProcessRunner()
    private val svgGenerator: SvgGenerator = SvgGeneratorImpl(processRunner)
    private val notifications: Notifications = LineEmittingNotifications(emitLine)
    private val simpleReportStyle: ReportStyle = SimpleReportStyle()
    private val tableReportStyle: ReportStyle = TableReportStyle()
    private val reportStyleMap: Map<String, ReportStyle> = mapOf(
        "simple" to simpleReportStyle,
        "table" to tableReportStyle
    )
    private val dotReportFormat: ReportFormat = DotReportFormat(reportStyleMap)
    private val loadSvgLines: (Path, Detail) -> List<String> = SvgLoader(files)
    private val htmlReportFormat: ReportFormat = HtmlReportFormat(config.sourcePrefix, loadSvgLines)
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(
        htmlReportFormat,
        dotReportFormat,
        files,
        svgGenerator,
        config.reportDir,
        config.reportStyleName
    )
    val runner: Runnable = Runner(
        config.inputFile,
        files,
        reportGenerator,
        notifications::timeTaken
    )
}
