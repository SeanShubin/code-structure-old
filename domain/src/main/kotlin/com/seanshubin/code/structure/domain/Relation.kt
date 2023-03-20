package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Comparators.nameListComparator

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

    fun narrowToScope(scope:List<String>):Relation? {
        val narrowedFirst = first.narrowToScope(scope) ?: return null
        val narrowedSecond = second.narrowToScope(scope) ?: return null
        if(narrowedFirst == narrowedSecond) return null
        return Relation(narrowedFirst, narrowedSecond)
    }

    override fun compareTo(other: Relation): Int = nameListComparator.compare(this.toList(), other.toList())
}
