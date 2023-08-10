package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.scanformatbeam.IntUtil.roundUpMod

data class BeamFile(
    val fileSize: Int,
    val atoms: List<String>,
    val imports: List<Import>,
    val sections: List<Section>
) {
    fun replaceAtom(original: String, newValue: String): BeamFile {
        val newAtoms = atoms.map {
            if (it == original) {
                newValue
            } else {
                it
            }
        }
        return copy(atoms = newAtoms)
    }

    fun computeSize(): Int = sections.sumOf { it.size.roundUpMod(4) + 8 } + 4
    val name: String get() = atoms[0]
    val dependencies: List<String>
        get() = imports.map { atoms[it.moduleIndex - 1] }.distinct()

    fun toLines(): List<String> = sizeLines() + atomLines() + importLines()
    private fun sizeLines(): List<String> = listOf("size = $fileSize")
    private fun atomLines(): List<String> = atoms.mapIndexed { index, atom ->
        "atom[${index + 1}] = $atom"
    }

    private fun importLines(): List<String> = imports.map { it.toLine() }
    private fun Import.toLine(): String {
        val module = atoms[moduleIndex - 1]
        val function = atoms[functionIndex - 1]
        return "import = $module.$function/$arity"
    }
}
