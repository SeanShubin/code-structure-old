package com.seanshubin.code.structure.scanformat

data class FileContents(
    val name: String,
    val source: String,
    val dependencies: List<String>
)
