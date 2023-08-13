package com.seanshubin.code.structure.scanformat

class SourceDependencyLoader(
    private val sourceFileParser: FileParser,
    private val sourceFileLoader: FileListLoader,
) : DependencyLoader {
    override fun loadModules(): List<DependencyModule> {
        val sourceFiles = sourceFileLoader.loadFileList()
        val sourceFileContents = sourceFiles.flatMap { sourceFileParser.parseFile(sourceFileLoader.dir, it) }
        return sourceFileContents.map { contents ->
            val name = contents.name
            val dependencies = contents.dependencies
            val binary = null
            val source = contents.source
            DependencyModule(name, dependencies, binary, source)
        }
    }
}
