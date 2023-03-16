package com.seanshubin.code.structure.domain

data class Report(val name: String, val lines: List<String>) {
    fun toLines():List<String> = listOf(name) + lines
}
