package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.FoldFunctions.collapseToList

data class Lookup(
    val names: List<Name>,
    val relations: List<Relation>,
    val nodes: List<Node>,
    val reversedNodes: List<Node>,
    val cycles: List<Cycle>,
) {
    private val nodeByName: Map<Name, Node> = nodes.associateBy { it.name }
    private val cycleByName: Map<Name, Cycle> =
        cycles.flatMap { cycle -> cycle.parts.map { name -> name to cycle } }.toMap()
    private val namesInRelation: List<Name> = relations.flatMap { it.toList() }.sorted().distinct()
    private val namesNotInRelation: List<Name> = names.filter { !namesInRelation.contains(it) }
    private val relationsByCycle: Map<Cycle, List<Relation>> =
        relations.mapNotNull(::toCycleAndRelation).fold(emptyMap(), ::collapseToList)
    private val relationsNotInCycle = relations.filter { toCycleAndRelation(it) == null }

    fun children(context: List<String>): List<Name> =
        descend(context).flatten().names

    fun depth(name: Name): Int {
        val node = nodeByName.getValue(name)
        val cycle = cycleByName[name]
        return if (cycle == null) {
            val dependsOn = node.dependsOn
            val maxDepthDependsOn = dependsOn.maxOfOrNull(::depth) ?: 0
            maxDepthDependsOn + 1
        } else {
            val dependsOn = dependsOnNames(cycle)
            val maxDepthDependsOn = dependsOn.maxOfOrNull(::depth) ?: 0
            maxDepthDependsOn + cycle.size
        }
    }

    fun breadth(name: Name): Int = nodeByName.getValue(name).dependsOn.size

    fun transitive(name: Name): Int = transitiveNames(name).size

    fun descendant(name: Name): Int = descendantNames(name).size

    fun dependsOnNames(context: List<String>, target: Name): List<Name> =
        descend(context).flatten().dependsOnNames(target)

    fun namesInCycle(context: List<String>, name: Name): List<Name> =
        descend(context).flatten().namesInCycle(name)

    fun report(context: List<String>): Report {
        val name = (listOf("dependencies") + context).joinToString("-") + ".txt"
        val lines = descend(context).flatten().report()
        return Report(name, lines)
    }

    fun toLines(): List<String> {
        val nameLines = names.map { it.simpleString }.map { "  $it" }
        val relationLines = relations.map { it.simpleString }.map { "  $it" }
        val nodeLines = nodes.map { it.simpleString }.map { "  $it" }
        val reversedNodeLines = reversedNodes.map { it.simpleString }.map { "  $it" }
        val cycleLines = cycles.map { it.simpleString }.map { "  $it" }
        return listOf("names") + nameLines +
                listOf("relations") + relationLines +
                listOf("nodes") + nodeLines +
                listOf("reversedNodes") + reversedNodeLines +
                listOf("cycles") + cycleLines
    }

    private fun descendantNames(name: Name): List<Name> = names.filter { it.startsWith(name) && it != name }

    private fun descend(target: String): Lookup {
        val newNames = names.mapNotNull { it.descend(target) }
        val newRelations = relations.mapNotNull { it.descend(target) }
        return fromNamesAndRelations(newNames, newRelations)
    }

    private fun descend(path: List<String>): Lookup {
        if (path.isEmpty()) return this
        val head = path.first()
        val tail = path.drop(1)
        return descend(head).descend(tail)
    }

    private fun flatten(): Lookup {
        val newNames = names.map { it.flatten() }
        val newRelations = relations.map { it.flatten() }
        return fromNamesAndRelations(newNames, newRelations)
    }

    private fun dependsOnNames(name: Name): List<Name> {
        val node = nodeByName.getValue(name)
        return node.dependsOn
    }

    private fun dependsOnNames(cycle: Cycle): List<Name> =
        cycle.parts.flatMap(::dependsOnNames).distinct().filter { !cycle.parts.contains(it) }

    private fun transitiveNames(name: Name): List<Name> {
        val node = nodeByName.getValue(name)
        val cycle = cycleByName[name]
        return if (cycle == null) {
            val immediate = node.dependsOn
            val descendents = immediate.flatMap(::transitiveNames)
            (immediate + descendents).sorted().distinct()
        } else {
            val dependsOn = dependsOnNames(cycle)
            val descendents = dependsOn.flatMap(::transitiveNames)
            (cycle.parts + dependsOn + descendents).sorted().distinct().filter { it != name }
        }
    }

    private fun namesInCycle(name: Name): List<Name> {
        return cycleByName[name]?.parts?.map { it } ?: emptyList()
    }

    private fun toCycleAndRelation(relation: Relation): Pair<Cycle, Relation>? {
        val (first, second) = relation
        val firstCycle = cycleByName[first] ?: return null
        val secondCycle = cycleByName[second] ?: return null
        if (firstCycle != secondCycle) return null
        return firstCycle to relation
    }

    private fun report(): List<String> {
        val header = listOf("digraph detangled {")
        val singles = namesNotInRelation.map {
            "  ${it.simpleString}"
        }
        val notInCycle = relationsNotInCycle.map { (first, second) ->
            "  ${first.simpleString} -> ${second.simpleString}"
        }
        val inCycle = relationsByCycle.toList().flatMapIndexed { index, (cycle, relations) ->
            val beginCycle = listOf(
                "  subgraph cluster_$index {",
                "    penwidth=2",
                "    pencolor=Red"
            )
            val cycleBody = relations.map { relation ->
                "    ${relation.first.simpleString} -> ${relation.second.simpleString}"
            }
            val endCycle = listOf(
                "  }"
            )
            beginCycle + cycleBody + endCycle
        }
        val footer = listOf("}")
        return header + singles + notInCycle + inCycle + footer
    }

    companion object {
        fun fromLines(lines: List<String>): Lookup {
            val (parsedNames, parsedRelations) = Format.parseInputLines(lines)
            return fromNamesAndRelations(parsedNames, parsedRelations)
        }

        private fun fromNamesAndRelations(originNames: List<Name>, originRelations: List<Relation>): Lookup {
            val names =
                (originNames + originRelations.flatMap { it.toList() }).flatMap { it.toHierarchy() }.sorted().distinct()
            val relations = originRelations.filter { it.first != it.second }.sorted().distinct()
            val reversedRelations = relations.map { it.reverse() }.sorted()
            val cycleLists = CycleUtil.findCycles(relations.map { it.toPair() }.toSet())
            val cycles = cycleLists.map { Cycle(it.toList().sorted()) }
            val nodes = constructNodes(names, relations)
            val reversedNodes = constructNodes(names, reversedRelations)
            return Lookup(names, relations, nodes, reversedNodes, cycles)
        }

        private fun constructNodes(names: List<Name>, relations: List<Relation>): List<Node> {
            val dependsOnMapNoEmpty = relations.map { it.toPair() }.fold(mapOf(), ::collapseToList)
            val dependsOnMap = names.map { name ->
                val existing = dependsOnMapNoEmpty[name]
                if (existing == null) {
                    name to emptyList<Name>()
                } else {
                    name to existing
                }
            }
            return dependsOnMap.map { (name, dependsOn) ->
                Node(name, dependsOn.sorted())
            }
        }
    }
}
