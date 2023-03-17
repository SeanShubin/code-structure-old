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

    val descendCache = mutableMapOf<String, Lookup>()

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

    fun reportableContexts(): List<List<String>> {
        val allContexts = listOf(listOf<String>()) + names.map { it.parts }
        return allContexts.filter { children(it).isNotEmpty() }
    }

    fun report(context: List<String>, makeLink: (Name) -> String?): Report {
        val name = (listOf("dependencies") + context).joinToString("-")
        val lines = descend(context).flatten().reportAll(makeLink)
        return Report(name, lines)
    }

    fun generateReports(): List<Report> {
        val contexts = reportableContexts()
        return contexts.map { context ->
            val makeLink = makeLinkFunction(contexts, context)
            report(context, makeLink)
        }
    }

    fun makeLinkFunction(contexts: List<List<String>>, context: List<String>): (Name) -> String? = { name: Name ->
        val target = context + name.parts
        if (contexts.contains(target)) {
            val link = (listOf("dependencies") + context + name.parts).joinToString("-")
            "$link.svg"
        } else {
            null
        }
    }

    private fun descendantNames(name: Name): List<Name> = names.filter { it.startsWith(name) && it != name }

    private fun descend(target: String): Lookup {
        val existing = descendCache[target]
        return if (existing == null) {
            val newNames = names.mapNotNull { it.descend(target) }
            val newRelations = relations.mapNotNull { it.descend(target) }
            val newValue = fromNamesAndRelations(newNames, newRelations)
            descendCache[target] = newValue
            descend(target)
        } else {
            existing
        }
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

    private fun reportAll(makeLink: (Name) -> String?): List<String> {
        fun Name.dotString(): String {
            val unquoted = if (this.parts.isEmpty()) {
                "--ancestors--"
            } else {
                parts.joinToString(".")
            }
            val quoted = "\"$unquoted\""
            return quoted
        }

        val header = listOf("digraph detangled {")
        val singles = names.map {
            val dotString = it.dotString()
            val link = makeLink(it)
            if (link == null) {
                "  $dotString"
            } else {
                "  $dotString [URL=\"$link\" fontcolor=Blue]"
            }
        }
        val notInCycle = relationsNotInCycle.map { (first, second) ->
            val firstDotString = first.dotString()
            val secondDotString = second.dotString()
            "  $firstDotString -> $secondDotString"
        }
        val inCycle = relationsByCycle.toList().flatMapIndexed { index, (cycle, relations) ->
            val beginCycle = listOf(
                "  subgraph cluster_$index {",
                "    penwidth=2",
                "    pencolor=Red"
            )
            val cycleBody = relations.map { relation ->
                val firstDotString = relation.first.dotString()
                val secondDotString = relation.second.dotString()
                "    $firstDotString -> $secondDotString"
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
