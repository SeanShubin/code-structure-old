package com.seanshubin.code.structure.domain

object FoldFunctions {
    fun <KeyType, ValueType> List<Pair<KeyType, ValueType>>.collapseToMapOfList():Map<KeyType, List<ValueType>> =
        fold(emptyMap(), ::collapseToMapOfList)

    fun <KeyType, ValueType> collapseToMapOfList(
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

    fun <KeyType, ValueType> flatCollapseToList(
        accumulator: Map<KeyType, List<ValueType>>,
        current: Pair<KeyType, List<ValueType>>
    ): Map<KeyType, List<ValueType>> {
        val key = current.first
        val existingValue = accumulator[key]
        val newValue = if (existingValue == null) {
            current.second
        } else {
            existingValue + current.second
        }
        val newEntry = key to newValue
        return accumulator + newEntry
    }
}