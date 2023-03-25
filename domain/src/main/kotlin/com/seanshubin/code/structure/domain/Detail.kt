package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Comparators.nameListComparator
import com.seanshubin.code.structure.domain.Comparators.pairCycleListOfRelationComparator
import com.seanshubin.code.structure.domain.FoldFunctions.collapseToMapOfList

interface Detail {
    val name: Name
    val source:String?
    val dependsOn: List<Detail>
    val dependedOnBy: List<Detail>
    val children: List<Detail>
    val cycleExcludingThis: List<Detail>
    val cycleIncludingThis: List<Detail>
    val thisOrCycleDependsOn: List<Detail>
    val thisOrCycleDependedOnBy: List<Detail>
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
        val allChildren = flattenChildren()
        val relations = allChildren.flatMap { a ->
            a.dependsOn.map { b ->
                Relation(a.name, b.name)
            }
        }.mapNotNull { it.narrowToScope(name.parts) }.sorted().distinct()
        val relationsWithReasons = relations.map{ relation ->
            val reasons = allChildren.flatMap { a ->
                a.dependsOn.mapNotNull { b ->
                    if(a.startsWith(relation.first) && b.startsWith(relation.second)){
                        Relation(a.name,b.name)
                    } else {
                        null
                    }
                }
            }
            RelationWithReasons(relation, reasons)
        }
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
        return RelationsByType(relationsWithReasons, relationsNotInCycle, relationsInCycle)
    }

    fun aggregateChildCount(): Int = flattenChildren().size

    fun aggregateDependsOnCount(): Int =
        aggregateDependencyCount { it.dependsOn }

    fun aggregateDependedOnByCount(): Int =
        aggregateDependencyCount { it.dependedOnBy }

    fun aggregateDepth(): Int {
        val allChildren = thisAndFlattenedChildren()
        return allChildren.maxOfOrNull { it.depth } ?: 0
    }

    fun aggregateTransitive(): Int {
        val allChildren = thisAndFlattenedChildren()
        return allChildren.maxOfOrNull { it.transitive } ?: 0
    }

    private fun aggregateDependencyCount(f: (Detail) -> List<Detail>): Int {
        val allChildren = thisAndFlattenedChildren()
        fun accumulateRelation(soFar: Set<Relation>, a: Detail): Set<Relation> {
            val relations = f(a).map { Relation(a.name, it.name) }
            val outsideRelations = relations.filterNot { relation ->
                relation.second.startsWith(name)
            }
            return soFar + outsideRelations
        }

        val empty = setOf<Relation>()
        val allRelations = allChildren.fold(empty, ::accumulateRelation)
        return allRelations.size
    }

    fun startsWith(other: Name): Boolean = name.startsWith(other)

    companion object {
        val depthAscending = Comparator<Detail> { o1, o2 -> o1.depth.compareTo(o2.depth) }
        val depthDescending = depthAscending.reversed()
        val nameAscending = Comparator<Detail> { o1, o2 -> o1.name.compareTo(o2.name) }
        val depthDescendingNameAscending = depthDescending.thenComparing(nameAscending)
    }
}
