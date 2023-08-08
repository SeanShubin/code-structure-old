package com.seanshubin.code.structure.domain

data class RelationsByType(
    val all: List<RelationWithReasons>,
    val notInCycle: List<Relation>,
    val cycles: List<Pair<List<Name>, List<Relation>>>
)
