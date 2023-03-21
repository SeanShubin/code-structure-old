package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface ReportGenerator {
    fun generateReports(detail: Detail, reportDir: Path, style: String)
}
