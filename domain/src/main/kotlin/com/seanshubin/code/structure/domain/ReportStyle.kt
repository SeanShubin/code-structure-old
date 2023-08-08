package com.seanshubin.code.structure.domain

enum class ReportStyle {
    SIMPLE {
        override fun makeShapeAttribute(detail: Detail): List<Pair<String, String>> {
            return emptyList()
        }

        override fun makeLabelAttribute(name: String, detail: Detail): List<Pair<String, String>> {
            val count = detail.aggregateChildCount()
            val text = if (count == 0) name else "$name ($count)"
            return listOf("label" to "\"$text\"")
        }
    },
    TABLE {
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
            val nameText = if (childCount == 0) name else "$name ($childCount)"
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
    };

    abstract fun makeShapeAttribute(detail: Detail): List<Pair<String, String>>
    abstract fun makeLabelAttribute(name: String, detail: Detail): List<Pair<String, String>>

    companion object {
        val prompt: String = "one of ${values().joinToString(", ")}"
        fun fromString(s: String): ReportStyle {
            val value = values().find { s.equals(it.name, ignoreCase = true) }
            return value ?: throw RuntimeException("Expected $prompt, got '$s'")
        }
    }
}
