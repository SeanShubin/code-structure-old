package com.seanshubin.code.structure.scanformat

enum class Style {
    BINARY,
    SOURCE;

    companion object {
        fun String.toStyle(): Style =
            enumValues<Style>().find { it.name.equals(this, ignoreCase = true) }!!
    }
}
