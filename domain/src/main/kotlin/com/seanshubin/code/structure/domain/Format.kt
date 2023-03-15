package com.seanshubin.code.structure.domain

object Format {
    private val namePattern = """\w+(?:\.\w+)*"""
    private val nameRegex = Regex(namePattern)
    private val relationRegex = Regex("""($namePattern)\s*->\s*($namePattern)""")
    fun parseName(line:String):Name? {
        nameRegex.matchEntire(line) ?: return null
        return Name(line.split('.'))
    }
    fun parseRelation(line:String):Relation? {
        val matchResult = relationRegex.matchEntire(line) ?: return null
        val first = Name(matchResult.groupValues[1].split('.'))
        val second =  Name(matchResult.groupValues[2].split('.'))
        return Relation(first, second)
    }
    fun parseInputLines(lines:List<String>):Pair<List<Name>, List<Relation>> {
        val names = mutableListOf<Name>()
        val relations = mutableListOf<Relation>()
        lines.forEachIndexed { index, line ->
            val name = parseName(line)
            if(name == null){
                val relation = parseRelation(line)
                if(relation == null){
                    throw RuntimeException("Input line '$line' at index $index did not match '$nameRegex' or '$relationRegex'")
                } else {
                    relations.add(relation)
                }
            } else {
                names.add(name)
            }
        }
        return Pair(names, relations)
    }
}
