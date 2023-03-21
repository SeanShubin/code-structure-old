package com.seanshubin.code.structure.domain

class SimpleReportStyle : ReportStyle {
    override fun makeShapeAttribute(detail: Detail): List<Pair<String, String>> {
        return emptyList()
    }

    override fun makeLabelAttribute(name: String, detail: Detail): List<Pair<String, String>> {
        val count = detail.aggregateChildCount()
        val text = if(count == 0) name else "$name ($count)"
        return listOf("label" to "\"$text\"")
    }
}

