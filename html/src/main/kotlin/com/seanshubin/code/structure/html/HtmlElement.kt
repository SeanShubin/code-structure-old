package com.seanshubin.code.structure.html

interface HtmlElement {
    fun toLines(): List<String>
    data class Tag(
        val tag: String,
        val children: List<HtmlElement> = emptyList(),
        val attributes: List<Pair<String, String>> = emptyList()
    ) : HtmlElement {
        private fun openTag(): String =
            if (attributes.isEmpty()) {
                "<$tag>"
            } else {
                val attributesString = attributes.joinToString(" ") { (name, value) ->
                    "$name=\"$value\""
                }
                "<$tag $attributesString>"
            }

        private fun closeTag(): String = "</$tag>"
        override fun toLines(): List<String> {
            val first = openTag()
            val last = closeTag()
            val middle = children.flatMap { it.toLines() }.map(::indent)
            return listOf(first) + middle + listOf(last)
        }

    }

    data class Text(
        val lines: List<String>
    ) : HtmlElement {
        override fun toLines(): List<String> = lines
    }

    companion object {
        fun h1(content: String): HtmlElement = element("h1", content)
        fun p(element: HtmlElement): HtmlElement = element("p", element)
        fun a(content: String, href: String): HtmlElement = element("a", content, "href" to href)
        fun html(title: String, vararg elements: HtmlElement): HtmlElement {
            val body = element("body", *elements)
            val titleElement = element("title", title)
            val head = element("head", titleElement)
            return element("html", head, body)
        }

        private fun element(tag: String, content: String): HtmlElement =
            Tag(tag, listOf(text(content)))

        private fun element(tag: String, vararg elements: HtmlElement): HtmlElement =
            Tag(tag, elements.toList())

        private fun element(tag: String, content: String, vararg attributes: Pair<String, String>): HtmlElement =
            Tag(tag, listOf(Text(listOf(content))), attributes.toList())

        private fun text(text: String): HtmlElement = Text(listOf(text))
        private fun indent(s: String): String = "  $s"
    }
}
