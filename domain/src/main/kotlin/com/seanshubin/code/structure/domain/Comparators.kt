package com.seanshubin.code.structure.domain

object Comparators {
    val stringListComparator = ListComparator<String>()
    val nameListComparator = ListComparator<Name>()
    val pairCycleListOfRelationComparator =
        Comparator<Pair<List<Name>, List<Relation>>> { o1, o2 -> nameListComparator.compare(o1.first, o2.first) }
}
