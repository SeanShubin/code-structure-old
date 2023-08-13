package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.scanformat.FileContents
import com.seanshubin.code.structure.scanformat.FileParser
import com.seanshubin.code.structure.scanformatbeam.BeamFileReader.toBeamFile
import java.nio.file.Path

class FileParserBinaryBeam(private val files: FilesContract) : FileParser {
    private val removeElixirPrefix = { s: String -> s.removePrefix("Elixir.") }
    private fun String.removeElixirPrefix(): String = removeElixirPrefix(this)
    override fun parseFile(baseDir: Path, file: Path): List<FileContents> {
        val beamFile = files.newInputStream(file).use {
            it.toBeamFile()
        }
        val module = beamFile.name.removeElixirPrefix()
        val relativeDir = baseDir.relativize(file)
        val source = relativeDir.toString()
        val dependencies = beamFile.dependencies.map(removeElixirPrefix)
        return listOf(FileContents(module, source, dependencies))
    }
}
