package com.seanshubin.code.structure.domain

class DetailLookup(val detailMap: Map<Name, DetailValue>, val value: DetailValue) : Detail {
    override val name: Name get() = value.name
    override val dependsOn: List<Detail>
        get() = value.dependsOn.map(toImpl)
    override val dependedOnBy: List<Detail>
        get() = value.dependedOnBy.map(toImpl)
    override val children: List<Detail>
        get() = value.children.map(toImpl)
    override val cycleExcludingThis: List<Detail>
        get() = value.cycleExcludingThis.map(toImpl)
    override val cycleIncludingThis: List<Detail>
        get() = value.cycleIncludingThis.map(toImpl)
    override val thisOrCycleDependsOn: List<Detail>
        get() = value.thisOrCycleDependsOn.map(toImpl)
    override val depth: Int
        get() = value.depth
    override val transitive: Int
        get() = value.transitive
    override val transitiveList: List<Detail>
        get() = value.transitiveList.map(toImpl)

    override fun toString(): String = if (name.parts.isEmpty()) "<root>" else name.parts.joinToString(".")

    private val toImpl: (Name) -> DetailLookup = { name ->
        DetailLookup(detailMap, detailMap.getValue(name))
    }
}
