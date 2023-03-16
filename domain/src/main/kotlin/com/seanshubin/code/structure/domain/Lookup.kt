package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.FoldFunctions.collapseToList

data class Lookup(
    val names: List<Name>,
    val relations: List<Relation>,
    val nodes: List<Node>,
    val reversedNodes: List<Node>,
    val cycles: List<Cycle>,
) {
    val nodeByName: Map<Name, Node> = nodes.associateBy { it.name }
    val cycleByName: Map<Name, Cycle> = cycles.flatMap{ cycle -> cycle.parts.map { name -> name to cycle}}.toMap()
    val namesInRelation: List<Name> = relations.flatMap{it.toList()}.sorted().distinct()
    val namesNotInRelation:List<Name> =names.filter { !namesInRelation.contains(it) }
    val relationsByCycle: Map<Cycle, List<Relation>> = relations.mapNotNull(::toCycleAndRelation).fold(emptyMap(), ::collapseToList)
    val relationsNotInCycle = relations.filter{ toCycleAndRelation(it) == null}

    fun descend(target:String):Lookup{
        val newNames = names.mapNotNull { it.descend(target) }
        val newRelations = relations.mapNotNull { it.descend(target) }
        return fromNamesAndRelations(newNames,newRelations)
    }

    fun descend(path:List<String>):Lookup{
        if(path.isEmpty()) return this
        val head = path.first()
        val tail = path.drop(1)
        return descend(head).descend(tail)
    }

    fun flatten():Lookup {
        val newNames = names.map { it.flatten() }
        val newRelations = relations.map { it.flatten() }
        return fromNamesAndRelations(newNames,newRelations)
    }

    fun children():List<String> =
        names.map { it.simpleString }

    fun children(context: List<String>): List<String> =
        descend(context).flatten().children()

    fun dependsOn(target:String):List<String> {
        val name = Name.fromString(target)
        val node = nodeByName[name]
        val dependsOn = if(node == null ){
            emptyList()
        } else {
            node.dependsOn
        }
        return dependsOn.map {it.simpleString}
    }

    fun dependsOn(context:List<String>, target:String):List<String> =
        descend(context).flatten().dependsOn(target)

    fun cycleFor(target:String):List<String> {
        val name = Name.fromString(target)
        return cycleByName[name]?.parts?.map{it.simpleString} ?: emptyList()
    }

    fun cycleFor(context:List<String>, target:String):List<String> =
        descend(context).flatten().cycleFor(target)

    private fun toCycleAndRelation(relation:Relation):Pair<Cycle, Relation>? {
        val (first, second) = relation
        val firstCycle = cycleByName[first] ?: return null
        val secondCycle = cycleByName[second] ?: return null
        if(firstCycle != secondCycle) return null
        return firstCycle to relation
    }

    fun report():List<String> {
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
                "    pencolor=Red")
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

    fun report(context:List<String>):Report {
        val name = (listOf("dependencies")+context).joinToString("-") + ".txt"
        val lines = descend(context).flatten().report()
        return Report(name, lines)
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
            return fromNamesAndRelations(parsedNames, parsedRelations)
        }

        private fun fromNamesAndRelations(originNames:List<Name>, originRelations:List<Relation>):Lookup {
            val names =
                (originNames + originRelations.flatMap { it.toList() }).flatMap { it.toHierarchy() }.sorted().distinct()
            val relations = originRelations.filter{it.first != it.second}.sorted().distinct()
            val reversedRelations = relations.map { it.reverse() }.sorted()
            val cycleLists = CycleUtil.findCycles(relations.map { it.toPair() }.toSet())
            val cycles = cycleLists.map { Cycle(it.toList().sorted()) }
            val nodes = constructNodes(names, relations)
            val reversedNodes = constructNodes(names, reversedRelations)
            return Lookup(names, relations, nodes, reversedNodes, cycles)
        }

        private fun constructNodes(names:List<Name>, relations: List<Relation>): List<Node> {
            val dependsOnMapNoEmpty = relations.map { it.toPair() }.fold(mapOf(), ::collapseToList)
            val dependsOnMap = names.map { name ->
                val existing = dependsOnMapNoEmpty[name]
                if(existing == null){
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
