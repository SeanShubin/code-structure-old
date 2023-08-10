package com.seanshubin.code.structure.datatypes

data class NameDto(val name: String) {
    fun toDomain(): Name = Name(name.split("."))

    companion object {
        fun String.toName() = NameDto(this).toDomain()
    }
}
