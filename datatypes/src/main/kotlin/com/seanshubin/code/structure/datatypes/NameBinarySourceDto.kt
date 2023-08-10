package com.seanshubin.code.structure.datatypes

data class NameBinarySourceDto(val name: String, val binary: String?, val source: String?) {
    fun toDomain(): NameBinarySource = NameBinarySource(
        NameDto(name).toDomain(),
        binary,
        source
    )
}
