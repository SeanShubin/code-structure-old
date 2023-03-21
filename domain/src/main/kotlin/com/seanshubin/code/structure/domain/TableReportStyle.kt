package com.seanshubin.code.structure.domain

class TableReportStyle : ReportStyle {
    override fun makeShapeAttribute(detail: Detail): List<Pair<String, String>> {
        return listOf("shape" to "plaintext")
    }

    override fun makeLabelAttribute(name: String, detail: Detail): List<Pair<String, String>> {
        val labelText = composeLabelText(name, detail)
        return listOf("label" to labelText)
    }

    private fun composeLabelText(name: String, detail: Detail): String {
        val dependedOnBy = detail.aggregateDependedOnByCount()
        val dependsOn = detail.aggregateDependsOnCount()
        val childCount = detail.aggregateChildCount()
        val depth = detail.aggregateDepth()
        val transitive = detail.aggregateTransitive()
        val nameText = if(childCount == 0) name else "$name ($childCount)"
        val labelText = """
        <
            <TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" CELLPADDING="4">
                <TR>
                    <TD COLSPAN="2">$nameText</TD>
                </TR>
                <TR>
                    <TD>$dependedOnBy</TD>
                    <TD>$dependsOn</TD>
                </TR>
                <TR>
                    <TD>$depth</TD>
                    <TD>$transitive</TD>
                </TR>
            </TABLE>
        >
        """.trimIndent()
        return labelText
    }
}
