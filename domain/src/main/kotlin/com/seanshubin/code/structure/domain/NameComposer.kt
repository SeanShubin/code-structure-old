package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.NameComposer.htmlBaseName

object NameComposer {
    fun List<String>.htmlDisplay():String =
        if(isEmpty()) "--root--"
        else joinToString(".")
    fun List<String>.htmlHref():String = "${htmlBaseName()}.html"
    fun List<String>.htmlBaseName():String = (listOf("dependencies") + this).joinToString("-")
    fun List<String>.htmlAnchor():String = """<a href="${htmlHref()}">${htmlDisplay()}</a>"""

    fun Name.htmlDisplay():String = parts.htmlDisplay()
    fun Name.htmlAnchor():String = parts.htmlAnchor()
    fun Name.htmlBaseName():String = parts.htmlBaseName()
    fun Detail.htmlBaseName():String = name.htmlBaseName()
    fun Detail.htmlDisplay():String = name.htmlDisplay()
    fun Detail.htmlAnchor():String = name.htmlAnchor()
    fun Relation.htmlDisplay():String = "${first.htmlDisplay()} -> ${second.htmlDisplay()}"
}
