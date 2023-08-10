package com.seanshubin.code.structure.scanformatbeam

object IntUtil {
    fun Int.roundUpMod(mod: Int): Int = this + paddingFor(mod)
    fun Int.paddingFor(mod: Int): Int = (mod - this % mod) % mod
}
