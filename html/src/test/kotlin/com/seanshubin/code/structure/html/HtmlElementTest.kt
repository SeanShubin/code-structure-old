package com.seanshubin.code.structure.html

import org.junit.Test
import kotlin.test.assertEquals

class HtmlElementTest {
    @Test
    fun singleLine() {
        // given
        val element = HtmlElement.h1("foo")
        val expected = """
            <h1>
              foo
            </h1>
        """.trimIndent()

        // when
        val actual = element.toLines().joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun attribute() {
        // given
        val element = HtmlElement.a("content", "link")
        val expected = """
            <a href="link">
              content
            </a>
        """.trimIndent()

        // when
        val actual = element.toLines().joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun nested() {
        // given
        val aElement = HtmlElement.a("the-content", "the-link")
        val pElement = HtmlElement.p(aElement)
        val expected = """
            <p>
              <a href="the-link">
                the-content
              </a>
            </p>
        """.trimIndent()

        // when
        val actual = pElement.toLines().joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun html() {
        // given
        val aElement = HtmlElement.a("the-link-name", "the-link")
        val pElement = HtmlElement.p(aElement)
        val titleElement = HtmlElement.title("the title")
        val headElement = HtmlElement.head(titleElement)
        val bodyElement = HtmlElement.body(pElement)
        val htmlElement = HtmlElement.html(headElement, bodyElement)
        val expected = """
            <html>
              <head>
                <title>
                  the title
                </title>
              </head>
              <body>
                <p>
                  <a href="the-link">
                    the-link-name
                  </a>
                </p>
              </body>
            </html>
        """.trimIndent()

        // when
        val actual = htmlElement.toLines().joinToString("\n")

        // then
        assertEquals(expected, actual)
    }
}