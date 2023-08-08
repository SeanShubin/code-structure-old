package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.FoldFunctions.collapseToMapOfList

object DetailBuilder {
    fun fromLines(lines: List<String>): Detail {
        val (parsedNames, parsedRelations) = Format.parseInputLines(lines)
        return fromNamesAndRelations(parsedNames, parsedRelations)
    }

    private fun fromNamesAndRelations(
        originNameSources: List<NameBinarySource>,
        originRelations: List<Relation>
    ): Detail {
        val originNames = originNameSources.map { it.name }
        val sourceByName = originNameSources.associate {
            it.name to it.source
        }
        val rootName = Name(emptyList())
        val relationNames = originRelations.flatMap { it.toList() }
        val leafNames = listOf(rootName) + originNames + relationNames
        val allNames = leafNames.flatMap { it.toHierarchy() }.sorted().distinct()
        val relations = originRelations.filter { it.first != it.second }.sorted().distinct()
        val nodes = constructNodes(allNames, relations)
        val nodeByName: Map<Name, Node> = nodes.associateBy { it.name }
        val reversedRelations = relations.map { it.reverse() }.sorted()
        val reversedNodes = constructNodes(allNames, reversedRelations)
        val reversedNodeByName: Map<Name, Node> = reversedNodes.associateBy { it.name }
        val cycleLists = CycleUtil.findCycles(relations.map { it.toPair() }.toSet())
        val cycles = cycleLists.map { it.toList().sorted() }
        val cycleByName: Map<Name, List<Name>> =
            cycles.flatMap { cycle -> cycle.map { name -> name to cycle } }.toMap()

        fun computeDependsOn(name: Name): List<Name> {
            val node = nodeByName.getValue(name)
            return node.dependsOn
        }

        fun computeDependedOnBy(name: Name): List<Name> {
            val node = reversedNodeByName.getValue(name)
            return node.dependsOn
        }

        fun computeChildren(parent: Name): List<Name> {
            val isChildOf = { child: Name ->
                val childExactlyOneHigher = child.parts.size == parent.parts.size + 1
                val prefixMatches = child.parts.take(parent.parts.size) == parent.parts
                childExactlyOneHigher && prefixMatches
            }
            val result = allNames.filter(isChildOf).sorted().distinct()
            return result
        }

        fun computeCycleExcludingThis(name: Name): List<Name> {
            val cycle = cycleByName[name] ?: emptyList()
            return cycle.filterNot { it == name }
        }

        fun computeCycleIncludingThis(name: Name): List<Name> =
            cycleByName[name] ?: emptyList()

        fun computeThisOrCycleDependsOn(name: Name): List<Name> {
            val thisOrCycle = cycleByName[name] ?: listOf(name)
            val dependenciesOutsideOfCycle = thisOrCycle.flatMap { current ->
                val node = nodeByName.getValue(current)
                node.dependsOn.filterNot { thisOrCycle.contains(it) }
            }.sorted().distinct()
            return dependenciesOutsideOfCycle
        }

        fun computeThisOrCycleDependedOnBy(name: Name): List<Name> {
            val thisOrCycle = cycleByName[name] ?: listOf(name)
            val dependenciesOutsideOfCycle = thisOrCycle.flatMap { current ->
                val node = reversedNodeByName.getValue(current)
                node.dependsOn.filterNot { thisOrCycle.contains(it) }
            }.sorted().distinct()
            return dependenciesOutsideOfCycle
        }

        fun computeTransitiveList(name: Name): List<Name> {
            val thisOrCycle = cycleByName[name] ?: listOf(name)
            val dependenciesOutsideOfCycle = thisOrCycle.flatMap { current ->
                val node = nodeByName.getValue(current)
                node.dependsOn.filterNot { thisOrCycle.contains(it) }
            }.distinct()
            val cycleDependencies = thisOrCycle.filterNot { it == name }
            val outsideTransitiveDependencies = dependenciesOutsideOfCycle.flatMap {
                computeTransitiveList(it)
            }
            return (cycleDependencies + dependenciesOutsideOfCycle + outsideTransitiveDependencies).sorted().distinct()
        }

        fun computeDepth(name: Name): Int {
            val thisOrCycle = cycleByName[name] ?: listOf(name)
            val dependenciesOutsideOfCycle = thisOrCycle.flatMap { current ->
                val node = nodeByName.getValue(current)
                node.dependsOn.filterNot { thisOrCycle.contains(it) }
            }.sorted().distinct()
            val max = dependenciesOutsideOfCycle.maxOfOrNull(::computeDepth) ?: -1
            return max + thisOrCycle.size
        }

        fun computeTransitive(name: Name): Int =
            computeTransitiveList(name).size

        fun computeDetail(name: Name): DetailValue {
            val source = sourceByName[name]
            val dependsOn: List<Name> = computeDependsOn(name)
            val dependedOnBy: List<Name> = computeDependedOnBy(name)
            val children: List<Name> = computeChildren(name)
            val cycleExcludingThis: List<Name> = computeCycleExcludingThis(name)
            val cycleIncludingThis: List<Name> = computeCycleIncludingThis(name)
            val thisOrCycleDependsOn: List<Name> = computeThisOrCycleDependsOn(name)
            val thisOrCycleDependedOnBy: List<Name> = computeThisOrCycleDependedOnBy(name)
            val depth: Int = computeDepth(name)
            val transitive: Int = computeTransitive(name)
            val transitiveList: List<Name> = computeTransitiveList(name)
            return DetailValue(
                name,
                source,
                dependsOn,
                dependedOnBy,
                children,
                cycleExcludingThis,
                cycleIncludingThis,
                thisOrCycleDependsOn,
                thisOrCycleDependedOnBy,
                depth,
                transitive,
                transitiveList
            )
        }

        val detailMap = allNames.map { name ->
            name to computeDetail(name)
        }.toMap()
        val rootValue = detailMap.getValue(rootName)
        return DetailLookup(detailMap, rootValue)
    }

    private fun constructNodes(names: List<Name>, relations: List<Relation>): List<Node> {
        val dependsOnMapNoEmpty = relations.map { it.toPair() }.fold(mapOf(), ::collapseToMapOfList)
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
