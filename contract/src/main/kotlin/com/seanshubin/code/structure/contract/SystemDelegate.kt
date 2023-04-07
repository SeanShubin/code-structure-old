package com.seanshubin.code.structure.contract

import java.io.Console
import java.io.InputStream
import java.io.PrintStream
import java.nio.channels.Channel
import java.util.*

object SystemDelegate : SystemContract {
    override fun setIn(`in`: InputStream) {
        System.setIn(`in`)
    }

    override fun setOut(out: PrintStream) {
        System.setOut(out)
    }

    override fun setErr(err: PrintStream) {
        System.setErr(err)
    }

    override fun console(): Console = System.console()
    override fun inheritedChannel(): Channel = System.inheritedChannel()
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun nanoTime(): Long = System.nanoTime()
    override fun arraycopy(src: Any, srcPos: Int, dest: Any, destPos: Int, length: Int) {
        System.arraycopy(src, srcPos, dest, destPos, length)
    }

    override fun identityHashCode(x: Any): Int = System.identityHashCode(x)
    override fun lineSeparator(): String? = System.lineSeparator()
    override fun getProperty(key: String): String? = System.getProperty(key)
    override fun getProperty(key: String, def: String): String = System.getProperty(key, def)
    override fun setProperty(key: String, value: String): String = System.setProperty(key, value)
    override fun clearProperty(key: String): String = System.clearProperty(key)
    override fun getenv(name: String): String = System.getenv(name)
    override fun getenv(): Map<String, String> = System.getenv()
    override fun getLogger(name: String): System.Logger = System.getLogger(name)
    override fun getLogger(name: String, bundle: ResourceBundle): System.Logger = System.getLogger(name, bundle)
    override fun exit(status: Int) {
        System.exit(status)
    }

    override fun gc() {
        System.gc()
    }

    override fun runFinalization() {
        System.runFinalization()
    }

    override fun load(filename: String) {
        System.load(filename)
    }

    override fun loadLibrary(libname: String) {
        System.loadLibrary(libname)
    }

    override fun mapLibraryName(libname: String): String = System.mapLibraryName(libname)
}
