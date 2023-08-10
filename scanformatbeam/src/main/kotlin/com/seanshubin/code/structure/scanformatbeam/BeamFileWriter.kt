package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.scanformatbeam.IntUtil.paddingFor
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object BeamFileWriter {
    fun OutputStream.writeAtoms(atoms: List<String>) {
        writeBytes("AtU8")
        val unpaddedSize = atoms.sumOf { it.length } + atoms.size
        val padding = unpaddedSize.paddingFor(4)
        writeInt(unpaddedSize + 4)
        writeInt(atoms.size)
        atoms.forEach {
            writeAtom(it)
        }
        writePadding(padding)
    }

    fun OutputStream.writePadding(padding: Int) {
        if (padding == 0) return
        write(0)
        writePadding(padding - 1)
    }

    fun OutputStream.writeAtom(atom: String) {
        val size = atom.length
        write(size)
        writeBytes(atom)
    }

    fun OutputStream.writeImports(imports: List<Import>) {
        writeBytes("ImpT")
        val size = imports.size * 12 + 4
        writeInt(size)
        writeInt(imports.size)
        imports.forEach { import ->
            writeInt(import.moduleIndex)
            writeInt(import.functionIndex)
            writeInt(import.arity)
        }
    }

    fun OutputStream.writeInt(x: Int) {
        this.write(ByteBuffer.allocate(4).putInt(x).array())
    }

    fun OutputStream.writeBytes(s: String) {
        write(s.toByteArray(StandardCharsets.UTF_8))
    }

    fun OutputStream.writeBeamFile(beamFile: BeamFile) {
        writeBytes("FOR1")
        writeInt(beamFile.computeSize())
        writeBytes("BEAM")
        beamFile.sections.forEach { section ->
            when (section.name) {
                "AtU8" -> {
                    writeAtoms(beamFile.atoms)
                }
                "ImpT" -> {
                    writeImports(beamFile.imports)
                }
                else -> {
                    writeSection(section)
                }
            }
        }
    }

    fun OutputStream.writeSection(section: Section) {
        writeBytes(section.name)
        writeInt(section.size)
        val padding = section.bytes.size.paddingFor(4)
        write(section.bytes.toByteArray())
        writePadding(padding)
    }
}