package com.seanshubin.code.structure.scanformat

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

abstract class DoNothingFileVisitor : FileVisitor<Path> {
    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult =
        FileVisitResult.CONTINUE

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult =
        FileVisitResult.CONTINUE

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult =
        FileVisitResult.CONTINUE

    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult =
        FileVisitResult.CONTINUE
}
