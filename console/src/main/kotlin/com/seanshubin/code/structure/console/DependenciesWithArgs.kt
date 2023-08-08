package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.contract.SystemContract
import com.seanshubin.code.structure.contract.SystemDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.process.ProcessRunner
import com.seanshubin.code.structure.process.SystemProcessRunner
import java.nio.file.Path
import java.nio.file.Paths

class DependenciesWithArgs(args: Array<String>) {
    private val configFilePathName = args.getOrNull(0) ?: "code-structure-config.json"
    private val configFilePath:Path = Paths.get(configFilePathName)
    private val files: FilesContract = FilesDelegate
    private val configuration: Configuration = JsonFileConfiguration(files, configFilePath)
    private val inputFile: Path = configuration.pathAt("generated/report/associations.txt", listOf("inputFile")).load()
    private val reportDir: Path = configuration.pathAt("generated/report/simple", listOf("reportDir")).load()
    private val sourcePrefix: String = configuration.stringAt("prefix-for-repository-source", listOf("sourcePrefix")).load()
    private val reportStyleName: String = configuration.stringAt(ReportStyle.prompt, listOf("reportStyleName")).load()
    private val emitLine: (String) -> Unit = ::println
    private val processRunner: ProcessRunner = SystemProcessRunner()
    private val svgGenerator: SvgGenerator = SvgGeneratorImpl(processRunner)
    private val notifications: Notifications = LineEmittingNotifications(emitLine)
    private val reportStyle:ReportStyle = ReportStyle.fromString(reportStyleName)
    private val dotReportFormat: ReportFormat = DotReportFormat(reportStyle)
    private val loadSvgLines: (Path, String) -> List<String> = SvgLoader(files)
    private val tableOfContentsReportFormat:ReportFormat = TableOfContentsReportFormat()
    private val listFormat:ReportFormat = ListReportFormat(sourcePrefix)
    private val entryPointFormat:ReportFormat = EntryPointReportFormat(sourcePrefix)
    private val htmlReportFormat: ReportFormat = HtmlReportFormat(sourcePrefix, loadSvgLines)
    private val cycleReportFormat:ReportFormat = CycleReportFormat(sourcePrefix)
    private val localCycleDotReportFormat:ReportFormat = LocalCycleDotReportFormat()
    private val localCycleHtmlReportFormat:ReportFormat = LocalCycleHtmlReportFormat(loadSvgLines)
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(
        htmlReportFormat,
        dotReportFormat,
        tableOfContentsReportFormat,
        listFormat,
        entryPointFormat,
        cycleReportFormat,
        localCycleDotReportFormat,
        localCycleHtmlReportFormat,
        files,
        svgGenerator,
        reportDir,
        reportStyleName
    )
    val system: SystemContract = SystemDelegate
    val runner: Runnable = Runner(
        inputFile,
        files,
        reportGenerator,
        system,
        notifications::timeTaken
    )
}
