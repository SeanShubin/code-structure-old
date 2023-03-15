package com.seanshubin.code.structure.domain

data class Name(val parts: List<String>) : Comparable<Name> {
    init {
        require(parts.isNotEmpty()){
            "Name parts must not be empty"
        }
    }
    constructor(vararg part: String) : this(part.toList())

    fun descend(target: String): Name? =
        if(parts.size ==1) null
        else if (startsWith(listOf(target))) copy(parts = parts.drop(1))
        else null
    fun flatten():Name = Name(parts.take(1))

    fun narrowTo(context: List<String>): String? =
        if (startsWith(context)) parts.drop(context.size)[0]
        else null

    val size: Int get() = parts.size
    val simpleString: String get() = parts.joinToString(".")
    fun startsWith(list: List<String>): Boolean {
        if (list.size > size) return false
        return list == parts.take(list.size)
    }

    fun startsWith(name: Name): Boolean = startsWith(name.parts)
    override fun compareTo(other: Name): Int =
        Comparators.ListComparator<String>().compare(this.parts, other.parts)

    fun toHierarchy():List<Name> =
        if(parts.size == 1) listOf(this)
        else toParent().toHierarchy() + this

    fun toParent(): Name = copy(parts = parts.dropLast(1))

    companion object {
        fun fromString(s: String): Name = Format.parseName(s)!!
    }
}
