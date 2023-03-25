package com.seanshubin.code.structure.domain

object NameComposer {
    fun List<String>.htmlDisplay():String =
        if(isEmpty()) "--root--"
        else joinToString(".")
    fun List<String>.htmlFileName():String = "${baseFileName()}.html"
    fun List<String>.baseFileName():String = (listOf("dependencies") + this).joinToString("-")
    fun List<String>.htmlAnchor():String = """<a href="${htmlFileName()}">${htmlDisplay()}</a>"""
    fun List<String>.dotFileName():String = baseFileName() + ".txt"
    fun List<String>.svgFileName():String = baseFileName() + ".svg"

    fun Name.htmlDisplay():String = parts.htmlDisplay()
    fun Name.htmlAnchor():String = parts.htmlAnchor()
    fun Name.baseFileName():String = parts.baseFileName()
    fun Name.dotFileName():String = parts.dotFileName()
    fun Name.svgFileName():String = parts.svgFileName()
    fun Name.htmlFileName():String = parts.htmlFileName()
    fun Detail.baseFileName():String = name.baseFileName()
    fun Detail.htmlDisplay():String = name.htmlDisplay()
    fun Detail.htmlAnchor():String = name.htmlAnchor()
    fun Detail.dotFileName():String = name.dotFileName()
    fun Detail.svgFileName():String = name.svgFileName()
    fun Detail.htmlFileName():String = name.htmlFileName()
    fun Detail.htmlSourceLink(prefix:String):String? = if(source == null) null else "$prefix$source"
    fun Detail.htmlSourceAnchor(prefix:String):String? {
        val htmlSourceLink = htmlSourceLink(prefix) ?: return null
        return """<a href="$htmlSourceLink">$source</a>"""
    }
    fun Relation.htmlDisplay():String = "${first.htmlDisplay()} -> ${second.htmlDisplay()}"
}
