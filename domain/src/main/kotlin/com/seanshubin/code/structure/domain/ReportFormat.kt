package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface ReportFormat {
    fun generateReports(reportDir: Path, detail: Detail): List<Report>
}
