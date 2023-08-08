package com.seanshubin.code.structure.domain

data class RelationWithReasons(
    val relation: Relation,
    val reasons: List<Relation>
)
