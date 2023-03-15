package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.FoldFunctions.collapseToList

data class Lookup(
    val names: List<Name>,
    val relations: List<Relation>,
    val nodes: List<Node>,
    val reversedNodes: List<Node>,
    val cycles: List<Cycle>,
) {
    fun children(context: List<String>): List<Name> {
        val size = context.size + 1
        return names.filter { it.startsWith(context) && it.size == size }
    }

    fun dependsOn(context:List<String>, target:String):List<String> {
        val targetPath = context + target
        val relevantNodes = nodes.filter{it.startsWith(targetPath)}
        val names = relevantNodes.flatMap { node ->
            node.dependsOn
        }.mapNotNull{ it.narrowTo(context) }.distinct().filter{
            it != target
        }
        return names
    }

    fun toLines(): List<String> {
        val nameLines = names.map { it.simpleString }.map{"  $it"}
        val relationLines = relations.map { it.simpleString }.map{"  $it"}
        val nodeLines = nodes.map { it.simpleString }.map{"  $it"}
        val reversedNodeLines = reversedNodes.map { it.simpleString }.map{"  $it"}
        val cycleLines = cycles.map { it.simpleString }.map{"  $it"}
        return listOf("names") + nameLines +
                listOf("relations") + relationLines +
                listOf("nodes") + nodeLines +
                listOf("reversedNodes") + reversedNodeLines +
                listOf("cycles") + cycleLines
    }

    companion object {
        fun fromLines(lines: List<String>): Lookup {
            val (parsedNames, parsedRelations) = Format.parseInputLines(lines)
            val names =
                (parsedNames + parsedRelations.flatMap { it.toList() }).flatMap { it.toHierarchy() }.sorted().distinct()
            val relations = parsedRelations.sorted().distinct()
            val reversedRelations = relations.map { it.reverse() }.sorted()
            val cycleLists = CycleUtil.findCycles(relations.map { it.toPair() }.toSet())
            val cycles = cycleLists.map { Cycle(it.toList().sorted()) }
            val nodes = constructNodes(relations)
            val reversedNodes = constructNodes(reversedRelations)
            return Lookup(names, relations, nodes, reversedNodes, cycles)
        }

        private fun constructNodes(relations: List<Relation>): List<Node> {
            val dependsOnMap = relations.map { it.toPair() }.fold(mapOf(), ::collapseToList)
            return dependsOnMap.map { (name, dependsOn) ->
                Node(name, dependsOn.sorted())
            }
        }
    }
}
