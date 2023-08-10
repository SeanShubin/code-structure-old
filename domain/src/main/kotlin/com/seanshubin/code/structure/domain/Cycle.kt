package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.datatypes.Name

data class Cycle(val parts: List<Name>) {
    val size: Int get() = parts.size
}

