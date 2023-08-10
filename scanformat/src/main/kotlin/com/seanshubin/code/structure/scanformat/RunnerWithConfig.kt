package com.seanshubin.code.structure.scanformat

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class RunnerWithConfig(
    private val generatedDir: Path,
    private val dependencyLoader: DependencyLoader,
    private val summarize: () -> Unit,
    private val files: FilesContract,
    private val reportGenerator: ReportGenerator
) : Runnable {
    override fun run() {
        files.createDirectories(generatedDir)
        val modules = dependencyLoader.loadModules().filter {
            it.source != null
        }.filterExternalDependencies()
        reportGenerator.generate(modules)
        summarize()
    }

    fun List<DependencyModule>.filterExternalDependencies(): List<DependencyModule> {
        val moduleByName = associateBy { it.name }
        return map { oldModule ->
            val newDependencies = oldModule.dependencies.filter(moduleByName::containsKey)
            oldModule.copy(dependencies = newDependencies)
        }
    }
}
