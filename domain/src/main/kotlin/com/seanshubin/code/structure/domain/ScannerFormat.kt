package com.seanshubin.code.structure.domain

enum class ScannerFormat {
    BEAM,
    CLASS;

    companion object {
        val prompt: String = "one of ${ScannerFormat.values().joinToString(", ")}"
        fun fromString(s: String): ScannerFormat {
            val value = ScannerFormat.values().find { s.equals(it.name, ignoreCase = true) }
            return value ?: throw RuntimeException("Expected $prompt, got '$s'")
        }
    }
}