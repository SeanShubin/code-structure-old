package com.seanshubin.code.structure.domain

interface ReportStyle {
    fun makeShapeAttribute(detail: Detail): List<Pair<String, String>>
    fun makeLabelAttribute(name: String, detail: Detail): List<Pair<String, String>>
}
