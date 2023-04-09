package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement

object GlobalHtml {
    fun standardHtml(title: String, vararg elements: HtmlElement): HtmlElement {
        val meta = HtmlElement.meta("charset" to "UTF-8")
        val titleElement = HtmlElement.title(title)
        val reset = HtmlElement.link("stylesheet", "reset.css")
        val style = HtmlElement.link("stylesheet", "dependencies.css")
        val head = HtmlElement.head(
            meta,
            titleElement,
            reset,
            style
        )
        val body = HtmlElement.body(*elements)
        return HtmlElement.html(head, body)
    }
}
