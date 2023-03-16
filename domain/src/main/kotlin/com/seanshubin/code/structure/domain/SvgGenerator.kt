package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface SvgGenerator {
    fun generate(directory:Path, dotFileName: String, svgFileName: String)
}
