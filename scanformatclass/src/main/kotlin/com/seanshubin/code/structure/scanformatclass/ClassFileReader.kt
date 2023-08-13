package com.seanshubin.code.structure.scanformatclass

import java.io.DataInputStream
import java.io.InputStream

object ClassFileReader {
    fun InputStream.toClassFile(): ClassFile =
        DataInputStream(this).use { dataInputStream ->
            val classFileInfo = ClassFileInfo.fromDataInput(dataInputStream)
            val thisClassName = classFileInfo.thisClassName
            val dependencyNames = classFileInfo.dependencyNames
            ClassFile(thisClassName, dependencyNames)
        }
}
