package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Paths

class RunnerWithArgs(
    private val args: Array<String>,
    private val createRunnable: (CodeStructureAppConfig)->Runnable,
) : Runnable {
    override fun run() {
        val inputFileName = args.getOrNull(0) ?: error("first parameter must be input file")
        val reportDirName = args.getOrNull(1) ?: error("second parameter must be report directory")
        val reportStyleName = args.getOrNull(2) ?: error("third parameter must be a report style name")
        val configuration = CodeStructureAppConfig(
            Paths.get(inputFileName),
            Paths.get(reportDirName),
            reportStyleName
        )
        val runner = createRunnable(configuration)
        runner.run()
    }
}
