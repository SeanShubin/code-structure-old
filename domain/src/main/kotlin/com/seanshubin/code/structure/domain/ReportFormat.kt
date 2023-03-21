package com.seanshubin.code.structure.domain

interface ReportFormat {
    fun report(detail: Detail): Report?
    fun generateReports(detail: Detail): List<Report>
}
