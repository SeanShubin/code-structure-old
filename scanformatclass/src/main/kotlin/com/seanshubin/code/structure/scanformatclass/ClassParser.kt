package com.seanshubin.code.structure.scanformatclass

import java.io.DataInput

interface ClassParser {
    fun parseClassDependencies(dataInput: DataInput): Pair<String, List<String>>
}
