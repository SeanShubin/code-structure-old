package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.contract.SystemContract
import com.seanshubin.code.structure.contract.SystemDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.domain.LineEmittingNotifications
import com.seanshubin.code.structure.domain.Notifications
import com.seanshubin.code.structure.domain.ReportGenerator
import com.seanshubin.code.structure.domain.ReportGeneratorImpl
import com.seanshubin.code.structure.logger.FileLogger
import com.seanshubin.code.structure.logger.Logger
import com.seanshubin.code.structure.process.ProcessRunner
import com.seanshubin.code.structure.process.SystemProcessRunner
import com.seanshubin.code.structure.scanformat.*
import com.seanshubin.code.structure.scanformatbeam.BeamScanner
import com.seanshubin.code.structure.scanformatbeam.FileParserBinary
import com.seanshubin.code.structure.scanformatbeam.FileParserSource
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

class DependenciesWithArgs(args: Array<String>, now: Instant) {
    private val configFilePathName = args.getOrNull(0) ?: "code-structure-config.json"
    private val configFilePath: Path = Paths.get(configFilePathName)
    private val files: FilesContract = FilesDelegate
    private val configuration: Configuration = JsonFileConfiguration(files, configFilePath)
    private val inputDir:Path = configuration.pathAt(".", listOf("inputDir")).load()
    private val outputDir:Path = configuration.pathAt("generated", listOf("outputDir")).load()
    private val associationsFile: Path = outputDir.resolve("associations.txt")
    private val reportDir: Path = outputDir.resolve("report")
    private val logDir: Path = outputDir.resolve("logs").resolve(now.toString())
    private val sourcePrefix: String =
        configuration.stringAt("prefix-for-repository-source", listOf("sourcePrefix")).load()
    private val reportStyleName: String = configuration.stringAt(ReportStyle.prompt, listOf("reportStyleName")).load()
    private val includeRegexPatternsBinary: List<String> =
        configuration.stringListAt("[]", listOf("binary", "includeRegexPatterns")).load()
    private val excludeRegexPatternsBinary: List<String> =
        configuration.stringListAt("[]", listOf("binary", "excludeRegexPatterns")).load()
    private val binaryFileListLoader: FileListLoader =
        RegexFileListLoader(inputDir, files, includeRegexPatternsBinary, excludeRegexPatternsBinary)
    private val includeRegexPatternsSource: List<String> =
        configuration.stringListAt("[]", listOf("source", "includeRegexPatterns")).load()
    private val excludeRegexPatternsSource: List<String> =
        configuration.stringListAt("[]", listOf("source", "excludeRegexPatterns")).load()
    private val sourceFileListLoader: FileListLoader =
        RegexFileListLoader(inputDir, files, includeRegexPatternsSource, excludeRegexPatternsSource)
    private val processRunner: ProcessRunner = SystemProcessRunner()
    private val svgGenerator: SvgGenerator = SvgGeneratorImpl(processRunner)
    private val logger: Logger = FileLogger(logDir, files)
    private val notifications: Notifications = LineEmittingNotifications(logger::emitLine)
    private val reportStyle: ReportStyle = ReportStyle.fromString(reportStyleName)
    private val dotReportFormat: ReportFormat = DotReportFormat(reportStyle)
    private val loadSvgLines: (Path, String) -> List<String> = SvgLoader(files)
    private val tableOfContentsReportFormat: ReportFormat = TableOfContentsReportFormat()
    private val listFormat: ReportFormat = ListReportFormat(sourcePrefix)
    private val entryPointFormat: ReportFormat = EntryPointReportFormat(sourcePrefix)
    private val htmlReportFormat: ReportFormat = HtmlReportFormat(sourcePrefix, loadSvgLines)
    private val cycleReportFormat: ReportFormat = CycleReportFormat(sourcePrefix)
    private val localCycleDotReportFormat: ReportFormat = LocalCycleDotReportFormat()
    private val localCycleHtmlReportFormat: ReportFormat = LocalCycleHtmlReportFormat(loadSvgLines)
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
    private val system: SystemContract = SystemDelegate
    private val fileParserBinary: FileParser = FileParserBinary(files)
    private val dependencyLineParser: DependencyLineParser = DependencyLineParserImpl()
    private val fileParserSource: FileParser = FileParserSource(
        files,
        dependencyLineParser,
        notifications::fileSuccessfullyParsed,
        notifications::wrongNumberOfModuleMatches,
        notifications::unableToParseDependencyLine

    )
    private val binaryLoader: DependencyLoader = BinaryDependencyLoader(
        binaryFileListLoader,
        sourceFileListLoader,
        fileParserBinary,
        fileParserSource,
    )
    private val associationsRepository: AssociationsRepository = AssociationsRepositoryImpl(
        associationsFile,
        files
    )
    private val scanner: Scanner = BeamScanner(
        binaryLoader,
        associationsRepository,
        notifications::summarize
    )
    val runner: Runnable = Runner(
        scanner,
        reportGenerator,
        system,
        associationsRepository,
        notifications::timeTaken
    )
}
