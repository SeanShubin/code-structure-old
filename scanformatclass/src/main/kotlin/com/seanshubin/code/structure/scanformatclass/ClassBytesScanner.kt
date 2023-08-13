package com.seanshubin.code.structure.scanformatclass

interface ClassBytesScanner {
    fun parseDependencies(classBytes: List<Byte>): Pair<String, List<String>>
}
