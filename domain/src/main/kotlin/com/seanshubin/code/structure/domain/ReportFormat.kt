package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface ReportFormat {
    fun report(reportDir: Path, detail: Detail, style: String): Report?
}
