package com.seanshubin.code.structure.scanformatbeam

import java.nio.ByteBuffer

object Conversions {
    fun List<Byte>.toInt(): Int = ByteBuffer.wrap(this.toByteArray()).int
}