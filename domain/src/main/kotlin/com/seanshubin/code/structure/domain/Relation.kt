package com.seanshubin.code.structure.domain

data class Relation(val first:Name, val second:Name):Comparable<Relation> {
    val simpleString:String get() = "${first.simpleString}->${second.simpleString}"
    fun toList():List<Name> = listOf(first, second)
    fun toPair():Pair<Name, Name> = Pair(first, second)
    fun reverse():Relation = Relation(second, first)
    override fun compareTo(other: Relation): Int =
        Comparators.ListComparator<Name>().compare(this.toList(), other.toList())
//    companion object {
//        fun fromString(s:String):Relation = Format.parseRelation(s)!!
//    }
}
