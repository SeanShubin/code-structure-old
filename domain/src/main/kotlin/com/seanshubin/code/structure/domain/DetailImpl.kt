package com.seanshubin.code.structure.domain

class DetailImpl(val detailMap: Map<Name, Detail>, val detail: Detail) : DetailContract {
    override val name: Name get() = detail.name
    override val dependsOn: List<DetailContract>
        get() = detail.dependsOn.map(toImpl)
    override val dependedOnBy: List<DetailContract>
        get() = detail.dependedOnBy.map(toImpl)
    override val children: List<DetailContract>
        get() = detail.children.map(toImpl)
    override val cycleExcludingThis: List<DetailContract>
        get() = detail.cycleExcludingThis.map(toImpl)
    override val cycleIncludingThis: List<DetailContract>
        get() = detail.cycleIncludingThis.map(toImpl)
    override val thisOrCycleDependsOn: List<DetailContract>
        get() = detail.thisOrCycleDependsOn.map(toImpl)
    override val depth: Int
        get() = detail.depth
    override val transitive: Int
        get() = detail.transitive
    override val transitiveList: List<DetailContract>
        get() = detail.transitiveList.map(toImpl)

    override fun toString(): String = if(name.parts.isEmpty()) "<root>" else name.parts.joinToString(".")

    private val toImpl: (Name) -> DetailImpl = { name ->
        DetailImpl(detailMap, detailMap.getValue(name))
    }
}
