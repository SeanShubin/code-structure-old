package com.seanshubin.code.structure.domain

data class Cycle(val parts:List<Name>) {
    val  simpleString:String get() = parts.joinToString(" ") { it.simpleString }
}

