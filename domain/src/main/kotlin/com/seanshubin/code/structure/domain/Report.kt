package com.seanshubin.code.structure.domain

data class Report(val baseName: String, val lines: List<String>) {
    fun toLines():List<String> = listOf(baseName) + lines
}
