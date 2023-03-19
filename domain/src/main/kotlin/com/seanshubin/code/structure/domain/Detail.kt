package com.seanshubin.code.structure.domain

interface Detail {
    val name: Name
    val dependsOn: List<Detail>
    val dependedOnBy: List<Detail>
    val children: List<Detail>
    val cycleExcludingThis: List<Detail>
    val cycleIncludingThis: List<Detail>
    val thisOrCycleDependsOn: List<Detail>
    val depth: Int
    val transitive: Int
    val transitiveList: List<Detail>
    fun thisAndFlattenedChildren(): List<Detail> =
        listOf(this) + flattenChildren()

    fun flattenChildren(): List<Detail> =
        children.flatMap { child ->
            listOf(child) + child.flattenChildren()
        }.sortedBy { it.name }.distinctBy { it.name }
}