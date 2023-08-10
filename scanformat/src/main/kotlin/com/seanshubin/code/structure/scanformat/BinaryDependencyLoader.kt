package com.seanshubin.code.structure.scanformat

class BinaryDependencyLoader(
    private val binaryFileLoader: FileListLoader,
    private val sourceFileLoader: FileListLoader,
    private val binaryFileParser: FileParser,
    private val sourceFileParser: FileParser
) : DependencyLoader {
    override fun loadModules(): List<DependencyModule> {
        val binaryFiles = binaryFileLoader.loadFileList()
        val sourceFiles = sourceFileLoader.loadFileList()
        val binaryFileContents = binaryFiles.mapNotNull { binaryFileParser.parseFile(binaryFileLoader.dir, it) }
        val sourceFileContents = sourceFiles.mapNotNull { sourceFileParser.parseFile(sourceFileLoader.dir, it) }
        val binaryByName = binaryFileContents.associateBy { it.name }
        val sourceByName = sourceFileContents.associateBy { it.name }
        val allNames = (binaryFileContents.map { it.name } + sourceFileContents.map { it.name }).sorted().distinct()
        return allNames.map { name ->
            val dependencies = binaryByName[name]?.dependencies ?: sourceByName[name]?.dependencies
            ?: throw RuntimeException("dependencies not found")
            val binary = binaryByName[name]?.source
            val source = sourceByName[name]?.source
            DependencyModule(name, dependencies, binary, source)
        }
    }
}
