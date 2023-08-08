package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class Report(
    val name: String,
    val lines: List<String>,
    val type: Type
) {
    enum class Type {
        HTML {
            override fun resolvePath(baseDir: Path, baseName: String): Path =
                baseDir.resolve("$baseName.html")
        },
        DOT {
            override fun resolvePath(baseDir: Path, baseName: String): Path =
                baseDir.resolve("$baseName.txt")
        };

        abstract fun resolvePath(baseDir: Path, baseName: String): Path
    }
}
