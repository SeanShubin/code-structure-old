package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.datatypes.Relation

data class RelationWithReasons(
    val relation: Relation,
    val reasons: List<Relation>
)
