package com.seanshubin.code.structure.domain

object FoldFunctions {
    fun <KeyType, ValueType> collapseToList(
        accumulator: Map<KeyType, List<ValueType>>,
        current: Pair<KeyType, ValueType>
    ): Map<KeyType, List<ValueType>> {
        val key = current.first
        val existingValue = accumulator[key]
        val newValue = if (existingValue == null) {
            listOf(current.second)
        } else {
            existingValue + current.second
        }
        val newEntry = key to newValue
        return accumulator + newEntry
    }
}