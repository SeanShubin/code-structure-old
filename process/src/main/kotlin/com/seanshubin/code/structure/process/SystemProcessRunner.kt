package com.seanshubin.code.structure.process

class SystemProcessRunner : ProcessRunner {
    override fun run(input: ProcessInput): ProcessOutput {
        val processBuilder = ProcessBuilder()
        processBuilder.command(*input.command.toTypedArray())
        processBuilder.directory(input.directory.toFile())
        val process = processBuilder.start()
        val outputStreamMonitor = InputStreamMonitor(process.inputStream)
        val errorStreamMonitor = InputStreamMonitor(process.errorStream)
        process.waitFor()
        return ProcessOutput(process.exitValue(), outputStreamMonitor.linesUtf8(), errorStreamMonitor.linesUtf8())
    }
}