package com.seanshubin.code.structure.contract

import java.io.Console
import java.io.InputStream
import java.io.PrintStream
import java.nio.channels.Channel
import java.util.*

interface SystemContract {
    fun setIn(`in`: InputStream)
    fun setOut(out: PrintStream)
    fun setErr(err: PrintStream)
    fun console(): Console
    fun inheritedChannel(): Channel
    fun currentTimeMillis(): Long
    fun nanoTime(): Long
    fun arraycopy(src: Any, srcPos: Int, dest: Any, destPos: Int, length: Int)
    fun identityHashCode(x: Any): Int
    fun lineSeparator(): String?
    fun getProperty(key: String): String?
    fun getProperty(key: String, def: String): String
    fun setProperty(key: String, value: String): String
    fun clearProperty(key: String): String
    fun getenv(name: String): String
    fun getenv(): Map<String, String>
    fun getLogger(name: String): java.lang.System.Logger
    fun getLogger(name: String, bundle: ResourceBundle): java.lang.System.Logger
    fun exit(status: Int)
    fun gc()
    fun runFinalization()
    fun load(filename: String)
    fun loadLibrary(libname: String)
    fun mapLibraryName(libname: String): String
}
