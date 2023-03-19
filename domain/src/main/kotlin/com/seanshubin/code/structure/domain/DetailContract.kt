package com.seanshubin.code.structure.domain

interface DetailContract {
    val name: Name
    val dependsOn: List<DetailContract>
    val dependedOnBy: List<DetailContract>
    val children: List<DetailContract>
    val cycleExcludingThis: List<DetailContract>
    val cycleIncludingThis: List<DetailContract>
    val thisOrCycleDependsOn: List<DetailContract>
    val depth: Int
    val transitive: Int
    val transitiveList: List<DetailContract>
    fun thisAndFlattenedChildren(): List<DetailContract> =
        listOf(this) + flattenChildren()

    fun flattenChildren(): List<DetailContract> =
        children.flatMap { child ->
            listOf(child) + child.flattenChildren()
        }.sortedBy { it.name }.distinctBy { it.name }
}