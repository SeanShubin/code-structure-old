package com.seanshubin.code.structure.scanformat

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class ReportGeneratorImpl(
    private val reportDir: Path,
    private val files: FilesContract
) : ReportGenerator {
    override fun generate(modules: List<DependencyModule>) {
        val names = modules.map {
            "${it.name} ${it.binary} ${it.source}"
        }
        val associations = modules.flatMap { left ->
            left.dependencies.map { right ->
                "${left.name} -> $right"
            }
        }
        val lines = names + associations
        val path = reportDir.resolve("associations.txt")
        files.createDirectories(reportDir)
        files.write(path, lines)
    }
}
