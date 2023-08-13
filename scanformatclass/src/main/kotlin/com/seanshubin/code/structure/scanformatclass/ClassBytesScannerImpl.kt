package com.seanshubin.code.structure.scanformatclass

import java.io.ByteArrayInputStream
import java.io.DataInputStream

class ClassBytesScannerImpl(
    private val classParser: ClassParser
) : ClassBytesScanner {
    override fun parseDependencies(classBytes: List<Byte>): Pair<String, List<String>> {
        val byteArrayInputStream = ByteArrayInputStream(classBytes.toByteArray())
        val dataInput = DataInputStream(byteArrayInputStream)
        return classParser.parseClassDependencies(dataInput)
    }
}
