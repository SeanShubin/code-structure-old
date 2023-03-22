package com.seanshubin.code.structure.domain

interface ReportFormat {
    fun report(detail: Detail, style: String): Report?
}
