package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.Runner
import com.seanshubin.code.structure.domain.SvgGenerator
import com.seanshubin.code.structure.domain.SvgGeneratorImpl
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
    val runner: Runnable = Runner(args, files, exit, emitLine, svgGenerator)
}