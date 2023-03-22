package com.seanshubin.code.structure.domain

data class Report(
    val baseName: String,
    val extension:String,
    val lines: List<String>,
    val isGraphSource:Boolean
)
