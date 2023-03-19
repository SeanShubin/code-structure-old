package com.seanshubin.code.structure.domain

class Detail(
    val name: Name,
    var nullableDependsOn: List<Detail>? = null,
    var nullableChildren: List<Detail>? = null,
    var nullableCycleExcludingThis: List<Detail>? = null,
    var nullableDepth: Int? = null,
    var nullableTransitive: Int? = null,
    var nullableTransitiveList: List<Detail>? = null
) {
    override fun toString(): String = name.toString()

    fun dependsOn():List<Detail> = nullableDependsOn!!
    fun children():List<Detail> = nullableChildren!!
    fun cycleExcludingThis():List<Detail> = nullableCycleExcludingThis!!
    fun flattenChildren(): List<Detail> =
        children().flatMap { child ->
            listOf(child) + child.flattenChildren()
        }.distinctBy { it.name }.sortedBy { it.name }

    fun cycleDependsOn(): List<Detail> {
        val cycle = cycleIncludingThis()
        val result = cycle.flatMap { cyclePart ->
            cyclePart.dependsOn().filterNot { cycle.contains(it) }
        }.distinctBy { it.name }.sortedBy { it.name }
        return result
    }

    fun cycleIncludingThis():List<Detail> = (cycleExcludingThis() + this).sortedBy { it.name }

    val depth: Int
        get() {
            if (nullableDepth == null) {
                nullableDepth = computeDepth()
            }
            return nullableDepth!!
        }
    val transitive: Int
        get() {
            if (nullableTransitive == null) {
                nullableTransitive = computeTransitive()
            }
            return nullableTransitive!!
        }
    val transitiveList:List<Detail> get() {
        if(nullableTransitiveList == null){
            nullableTransitiveList = computeTransitiveList()
        }
        return nullableTransitiveList!!
    }

    fun computeDepth(): Int {
        val cycleExcludingThis = cycleExcludingThis()
        val dependsOn = cycleDependsOn()
        val result = if (cycleExcludingThis.isEmpty()) {
            if (dependsOn.isEmpty()) {
                0
            } else {
                val max = dependsOn.map { it.computeDepth() }.maxOrNull() ?: 0
                max + 1
            }
        } else {
            val cycleDependsOn = cycleDependsOn()
            if (cycleDependsOn.isEmpty()) {
                cycleExcludingThis.size
            } else {
                val max = dependsOn.map { it.computeDepth() }.maxOrNull() ?: 0
                max + cycleExcludingThis.size + 1
            }
        }
        return result
    }

    fun computeTransitive(): Int =
        computeTransitiveList().size

    fun computeTransitiveList(): List<Detail> {
        val thisLevel = cycleExcludingThis()
        val cycleDependsOn = cycleDependsOn()
        val remain = cycleDependsOn.flatMap {
            it.transitiveList
        }
        val result = (thisLevel + cycleDependsOn + remain).sortedBy { it.name }.distinctBy { it.name }
        return result
    }
}
