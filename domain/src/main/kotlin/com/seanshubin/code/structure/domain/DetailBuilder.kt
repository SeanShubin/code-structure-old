package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.FoldFunctions.collapseToList

object DetailBuilder {
    fun fromLines(lines: List<String>): Detail {
        val (parsedNames, parsedRelations) = Format.parseInputLines(lines)
        return fromNamesAndRelations(parsedNames, parsedRelations)
    }

    private fun fromNamesAndRelations(originNames: List<Name>, originRelations: List<Relation>): Detail {
        val allNames =
            (originNames + originRelations.flatMap { it.toList() }).flatMap { it.toHierarchy() }.sorted().distinct()
        val relations = originRelations.filter { it.first != it.second }.sorted().distinct()
        val reversedRelations = relations.map { it.reverse() }.sorted()
        val cycleLists = CycleUtil.findCycles(relations.map { it.toPair() }.toSet())
        val cycles = cycleLists.map { Cycle(it.toList().sorted()) }
        val nodes = constructNodes(allNames, relations)
        val reversedNodes = constructNodes(allNames, reversedRelations)
        val nodeByName: Map<Name, Node> = nodes.associateBy { it.name }
        val reversedNodeByName:Map<Name, Node> = reversedNodes.associateBy { it.name }
        val cycleByName: Map<Name, Cycle> =
            cycles.flatMap { cycle -> cycle.parts.map { name -> name to cycle } }.toMap()

        fun isChildOf(parent: Name): (Name) -> Boolean = { child ->
            val childExactlyOneHigher = child.parts.size == parent.parts.size + 1
            val prefixMatches = child.parts.take(parent.parts.size) == parent.parts
            childExactlyOneHigher && prefixMatches
        }
        val mutableDetailMap = allNames.map { name ->
            name to Detail(name)
        }.toMap()
        fun computeDependsOn(name:Name):List<Detail> =
            nodeByName.getValue(name).dependsOn.map{mutableDetailMap.getValue(it)}
        fun computeDependedOnBy(name:Name):List<Detail> =
            reversedNodeByName.getValue(name).dependsOn.map{mutableDetailMap.getValue(it)}
        fun computeChildren(name:Name):List<Detail> =
            allNames.filter(isChildOf(name)).map{mutableDetailMap.getValue(it)}
        fun computeCycleExcludingThis(name:Name):List<Detail> =
            (cycleByName[name]?.parts?.filterNot{it == name} ?: emptyList()).map{mutableDetailMap.getValue(it)}

        allNames.forEach { name ->
            val mutable = mutableDetailMap.getValue(name)
            mutable.nullableDependsOn = computeDependsOn(name)
            mutable.nullableDependedOnBy = computeDependedOnBy(name)
            mutable.nullableChildren = computeChildren(name)
            mutable.nullableCycleExcludingThis = computeCycleExcludingThis(name)
        }

        val rootName = Name(emptyList())
        val rootDetail = Detail(
            name = rootName,
            nullableChildren = computeChildren(rootName)
        )
        return rootDetail
    }

    private fun constructNodes(names: List<Name>, relations: List<Relation>): List<Node> {
        val dependsOnMapNoEmpty = relations.map { it.toPair() }.fold(mapOf(), ::collapseToList)
        val dependsOnMap = names.map { name ->
            val existing = dependsOnMapNoEmpty[name]
            if (existing == null) {
                name to emptyList()
            } else {
                name to existing
            }
        }
        return dependsOnMap.map { (name, dependsOn) ->
            Node(name, dependsOn.sorted())
        }
    }
}
