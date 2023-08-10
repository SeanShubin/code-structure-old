package com.seanshubin.code.structure.scanformat

data class DependencyModule(
    val name: String,
    val dependencies: List<String>,
    val binary: String?,
    val source: String?
)
