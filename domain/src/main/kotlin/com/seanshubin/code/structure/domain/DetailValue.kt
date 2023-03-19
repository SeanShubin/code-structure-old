package com.seanshubin.code.structure.domain

data class DetailValue(
    val name: Name,
    val dependsOn: List<Name>,
    val dependedOnBy: List<Name>,
    val children: List<Name>,
    val cycleExcludingThis: List<Name>,
    val cycleIncludingThis: List<Name>,
    val thisOrCycleDependsOn: List<Name>,
    val depth: Int,
    val transitive: Int,
    val transitiveList: List<Name>
)
