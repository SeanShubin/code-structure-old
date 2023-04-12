package com.seanshubin.code.structure.domain

data class DotModel(val singles:List<Single>, val relations:List<Relation>){
    fun toDotLines():List<String>{
        val singlesLines = singles.map{it.toDotLine()}
        val relationLines = relations.map{it.toDotLine()}
        val contentLines = singlesLines + relationLines
        val allLines = wrapDigraph(contentLines)
        return allLines
    }

    private fun Name.toDotString():String {
        val nameString = parts.joinToString(".")
        return "\"$nameString\""
    }
    private fun Single.toDotString():String = name.toDotString()
    private fun Single.toDotLine():String =
        if(attributes.isEmpty()){
            toDotString() + ";"
        } else {
            val attributesString = attributes.joinToString(" ", "[", "]"){
                (name, value) ->
                "$name=\"$value\""
            }
            val dotString = toDotString()
            "$dotString $attributesString;"
        }
    private fun Relation.toDotLine():String =
        "${first.toDotString()} -> ${second.toDotString()};"

    private fun wrapDigraph(lines:List<String>):List<String>{
        val header = listOf("digraph detangled {")
        val body = (listOf("bgcolor=lightgray") + lines).map{"  $it"}
        val footer = listOf("}")
        return header + body + footer
    }
    data class Single(val name:Name, val attributes:List<Pair<String, String>> = emptyList())
}
