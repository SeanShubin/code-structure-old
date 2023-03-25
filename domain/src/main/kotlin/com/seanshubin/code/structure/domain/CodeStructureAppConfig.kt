package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class CodeStructureAppConfig(
    val inputFile: Path,
    val reportDir: Path,
    val reportStyleName: String
)
