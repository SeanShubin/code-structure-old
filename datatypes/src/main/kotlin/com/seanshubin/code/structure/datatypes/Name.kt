package com.seanshubin.code.structure.datatypes

import com.seanshubin.code.structure.datatypes.Comparators.stringListComparator

data class Name(val parts: List<String>) : Comparable<Name> {
    fun descend(target: String): Name? =
        if (parts.isEmpty()) null
        else if (startsWith(listOf(target))) copy(parts = parts.drop(1))
        else null

    fun flatten(): Name = Name(parts.take(1))

    fun startsWith(list: List<String>): Boolean {
        if (list.size > parts.size) return false
        return list == parts.take(list.size)
    }

    fun startsWith(name: Name): Boolean = startsWith(name.parts)
    override fun compareTo(other: Name): Int = stringListComparator.compare(this.parts, other.parts)

    fun toHierarchy(): List<Name> =
        if (parts.size < 2) listOf(this)
        else toParent().toHierarchy() + this

    fun narrowToScope(scope: List<String>): Name? {
        if (!startsWith(scope)) return null
        if (parts.size <= scope.size) return null
        return Name(parts.take(scope.size + 1))
    }

    private fun toParent(): Name = copy(parts = parts.dropLast(1))
}
