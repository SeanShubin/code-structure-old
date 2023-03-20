package com.seanshubin.code.structure.domain

data class RelationsByType(
    val notInCycle: List<Relation>,
    val cycles: List<Pair<List<Name>, List<Relation>>>
)
