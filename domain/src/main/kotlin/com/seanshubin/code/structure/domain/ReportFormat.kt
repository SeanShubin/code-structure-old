package com.seanshubin.code.structure.domain

interface ReportFormat {
    fun report(detail: Detail, style:String): Report?
    fun generateReports(detail: Detail, style:String): List<Report>
}
