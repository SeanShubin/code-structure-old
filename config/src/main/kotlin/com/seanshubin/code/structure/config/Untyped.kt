package com.seanshubin.code.structure.config

class Untyped(val value: Any?) {
    fun hasValueAtPath(vararg keys: Any?): Boolean =
        if (keys.isEmpty()) {
            true
        } else {
            if (valueIsMap()) {
                val mapValue = valueAsMap()
                val key = keys[0]
                if (mapValue.containsKey(key)) {
                    val innerValue = mapValue.getValue(key)
                    val remainingKeys = keys.drop(1).toTypedArray()
                    Untyped(innerValue).hasValueAtPath(*remainingKeys)
                } else {
                    false
                }
            } else {
                false
            }
        }

    fun getValueAtPath(vararg keys: Any?): Any? =
        if (keys.isEmpty()) {
            value
        } else {
            val mapValue = valueAsMap()
            val key = keys[0]
            val innerValue = mapValue.getValue(key)
            val remainingKeys = keys.drop(1).toTypedArray()
            Untyped(innerValue).getValueAtPath(*remainingKeys)
        }

    fun setValueAtPath(valueToSet:Any?, vararg keys:Any?):Untyped =
        if(keys.isEmpty()) {
            Untyped(valueToSet)
        } else {
            if(valueIsMap()){
                val key = keys[0]
                val mapValue = valueAsMap()
                val innerValue = if(mapValue.containsKey(key)){
                    mapValue.getValue(key)
                } else {
                    emptyMap<Any?, Any?>()
                }
                val remainingKeys = keys.drop(1).toTypedArray()
                val newInnerUntyped = Untyped(innerValue).setValueAtPath(valueToSet, *remainingKeys)
                val newInnerValue = newInnerUntyped.value
                val newPair = key to newInnerValue
                val newValue = mapValue + newPair
                Untyped(newValue)
            } else {
                Untyped(mapOf<Any?, Any?>()).setValueAtPath(valueToSet, *keys)
            }
        }

    private fun valueIsMap(): Boolean = value is Map<*, *>

    @Suppress("UNCHECKED_CAST")
    private fun valueAsMap(): Map<Any?, Any?> = value as Map<Any?, Any?>
}
