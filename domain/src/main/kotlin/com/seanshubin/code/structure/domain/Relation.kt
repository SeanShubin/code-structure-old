package com.seanshubin.code.structure.domain

data class Relation(val first: Name, val second: Name) : Comparable<Relation> {
    fun toList(): List<Name> = listOf(first, second)
    fun toPair(): Pair<Name, Name> = Pair(first, second)
    fun reverse(): Relation = Relation(second, first)
    fun descend(target: String): Relation? {
        val newFirst = first.descend(target) ?: return null
        val newSecond = second.descend(target) ?: return null
        return Relation(newFirst, newSecond)
    }

    fun flatten(): Relation = Relation(
        first.flatten(),
        second.flatten()
    )

    override fun compareTo(other: Relation): Int =
        Comparators.ListComparator<Name>().compare(this.toList(), other.toList())
//    companion object {
//        fun fromString(s:String):Relation = Format.parseRelation(s)!!
//    }
}
