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
        fun pre(lines: List<String>): HtmlElement = element("pre", lines.joinToString("\n"))
        fun p(element: HtmlElement): HtmlElement = element("p", element)
        fun head(vararg elements: HtmlElement): HtmlElement = element("head", *elements)
        fun body(vararg elements: HtmlElement): HtmlElement = element("body", *elements)
        fun div(elements: List<HtmlElement>): HtmlElement = element("div", elements)
        fun div(vararg elements: HtmlElement): HtmlElement = div(*elements)
        fun title(content: String): HtmlElement = element("title", content)
        fun a(content: String, href: String): HtmlElement = element("a", content, "href" to href)
        fun html(head:HtmlElement, body:HtmlElement): HtmlElement {
            return element("html", head, body)
        }
        fun table(headerRows:List<HtmlElement>, bodyRows:List<HtmlElement>):HtmlElement =
            element("table", thead(headerRows), tbody(bodyRows))
        fun thead(rows:List<HtmlElement>):HtmlElement = element("thead", rows)
        fun tbody(rows:List<HtmlElement>):HtmlElement = element("tbody", rows)
        fun th(s:String):HtmlElement = element("th", s)
        fun td(s:String):HtmlElement = element("td", s)
        fun td(element:HtmlElement):HtmlElement = element("td", element)
        fun tr(cells:List<HtmlElement>):HtmlElement = element("tr", cells)
        fun meta(vararg attributes:Pair<String, String>):HtmlElement =
            Tag("meta", attributes=attributes.toList())
        fun link(rel:String, href:String):HtmlElement =
            Tag("link", attributes = listOf("rel" to rel, "href" to href))
        fun legend(caption:String):HtmlElement = element("legend", caption)
        fun fieldset(caption:String, vararg elements:HtmlElement):HtmlElement {
            val legend = legend(caption)
            val allElements = listOf(legend) + elements
            return element("fieldset", allElements)
        }

        private fun element(tag: String, content: String): HtmlElement =
            Tag(tag, listOf(text(content)))

        private fun element(tag: String, elements: List<HtmlElement>): HtmlElement =
            Tag(tag, elements)
        private fun element(tag: String, vararg elements: HtmlElement): HtmlElement =
            element(tag, elements.toList())

        private fun element(tag: String, content: String, vararg attributes: Pair<String, String>): HtmlElement =
            Tag(tag, listOf(Text(listOf(content))), attributes.toList())

        private fun text(text: String): HtmlElement = Text(listOf(text))
        private fun indent(s: String): String = "  $s"
    }
}
