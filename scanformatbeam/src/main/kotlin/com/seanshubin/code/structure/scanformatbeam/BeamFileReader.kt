package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.scanformatbeam.InputStreamUtil.consumeBytes
import com.seanshubin.code.structure.scanformatbeam.InputStreamUtil.consumeInt
import com.seanshubin.code.structure.scanformatbeam.InputStreamUtil.consumeStringLiteral
import com.seanshubin.code.structure.scanformatbeam.InputStreamUtil.consumeStringOfSize
import com.seanshubin.code.structure.scanformatbeam.InputStreamUtil.consumeStringOfSizeOrNull
import java.io.ByteArrayInputStream
import java.io.InputStream

// https://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html
object BeamFileReader {
    fun InputStream.consumeSectionOrNull(): Section? {
        val name = consumeStringOfSizeOrNull(4) ?: return null
        val size = consumeInt()
        val bytes = consumeBytes(size)
        return Section(name, size, bytes)
    }

    fun InputStream.consumeSections(): List<Section> {
        val sections = mutableListOf<Section>()
        var section = consumeSectionOrNull()
        while (section != null) {
            sections.add(section)
            section = consumeSectionOrNull()
        }
        return sections
    }

    fun InputStream.consumeAtom(): String {
        val size = read()
        if (size == -1) {
            throw RuntimeException("Expected atom size, got end of file")
        }
        return consumeStringOfSize(size)
    }

    fun InputStream.consumeAtoms(): List<String> {
        val atomCount = consumeInt()
        val atoms = mutableListOf<String>()
        (0 until atomCount).forEach {
            val atom = consumeAtom()
            atoms.add(atom)
        }
        return atoms
    }

    fun InputStream.consumeImports(): List<Import> {
        val importCount = consumeInt()
        val imports = mutableListOf<Import>()
        (0 until importCount).forEach {
            val moduleIndex = consumeInt()
            val functionIndex = consumeInt()
            val arity = consumeInt()
            val import = Import(moduleIndex, functionIndex, arity)
            imports.add(import)
        }
        return imports
    }

    fun InputStream.toBeamFile(): BeamFile {
        consumeStringLiteral("FOR1")
        val fileSize = consumeInt()
        consumeStringLiteral("BEAM")
        val sections = consumeSections()
        val sectionByName = sections.associateBy { it.name }
        val atomsSection = sectionByName.getValue("AtU8")
        val atomInputStream = ByteArrayInputStream(atomsSection.bytes.toByteArray())
        val atoms = atomInputStream.consumeAtoms()
        val importsSection = sectionByName.getValue("ImpT")
        val importsInputStream = ByteArrayInputStream(importsSection.bytes.toByteArray())
        val imports = importsInputStream.consumeImports()
        return BeamFile(fileSize, atoms, imports, sections)
    }
}