package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Comparators.nameListComparator
import com.seanshubin.code.structure.domain.Comparators.pairCycleListOfRelationComparator
import com.seanshubin.code.structure.domain.FoldFunctions.collapseToMapOfList

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

    fun relations(): RelationsByType {
        val relations = flattenChildren().flatMap { a ->
            a.dependsOn.map { b ->
                Relation(a.name, b.name)
            }
        }.mapNotNull { it.narrowToScope(name.parts) }.sorted().distinct()
        val edges = relations.map { it.toPair() }.toSet()
        val cycles: List<List<Name>> = CycleUtil.findCycles(edges).map {
            it.toList().sorted()
        }.toList().sortedWith(nameListComparator)
        val cyclesByName = cycles.flatMap { cycle ->
            cycle.map { name ->
                name to cycle
            }
        }.toMap()
        val notACycle = emptyList<Name>()
        val relationsByCycle: Map<List<Name>, List<Relation>> = relations.map { relation ->
            val cycleForRelationFirst = cyclesByName[relation.first] ?: notACycle
            val cycleForRelationSecond = cyclesByName[relation.second] ?: notACycle
            if (cycleForRelationFirst == cycleForRelationSecond) {
                cycleForRelationFirst to relation
            } else {
                notACycle to relation
            }
        }.collapseToMapOfList()
        val relationsNotInCycle: List<Relation> = relationsByCycle[notACycle] ?: emptyList()
        val relationsInCycle: List<Pair<List<Name>, List<Relation>>> = relationsByCycle.filterNot { (first, _) ->
            first == notACycle
        }.toList().sortedWith(pairCycleListOfRelationComparator)
        return RelationsByType(relationsNotInCycle, relationsInCycle)
    }
}
