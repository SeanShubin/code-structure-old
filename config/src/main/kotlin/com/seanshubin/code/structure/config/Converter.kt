package com.seanshubin.code.structure.config

interface Converter<T> {
    val sourceType: Class<*>
    fun convert(value: Any?): T?
}
