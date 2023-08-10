package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.datatypes.Name
import com.seanshubin.code.structure.datatypes.Relation

data class RelationsByType(
    val all: List<RelationWithReasons>,
    val notInCycle: List<Relation>,
    val cycles: List<Pair<List<Name>, List<Relation>>>
)
