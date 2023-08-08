package com.seanshubin.code.structure.contract.test

import com.seanshubin.code.structure.contract.SystemContract
import java.io.Console
import java.io.InputStream
import java.io.PrintStream
import java.nio.channels.Channel
import java.util.*

interface SystemContractUnsupportedOperation : SystemContract {
    override fun setIn(`in`: InputStream) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setOut(out: PrintStream) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setErr(err: PrintStream) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun console(): Console {
        throw UnsupportedOperationException("not implemented")
    }

    override fun inheritedChannel(): Channel {
        throw UnsupportedOperationException("not implemented")
    }

    override fun currentTimeMillis(): Long {
        throw UnsupportedOperationException("not implemented")
    }

    override fun nanoTime(): Long {
        throw UnsupportedOperationException("not implemented")
    }

    override fun arraycopy(src: Any, srcPos: Int, dest: Any, destPos: Int, length: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun identityHashCode(x: Any): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun lineSeparator(): String? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getProperty(key: String): String? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getProperty(key: String, def: String): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setProperty(key: String, value: String): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun clearProperty(key: String): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getenv(name: String): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getenv(): Map<String, String> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getLogger(name: String): System.Logger {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getLogger(name: String, bundle: ResourceBundle): System.Logger {
        throw UnsupportedOperationException("not implemented")
    }

    override fun exit(status: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun gc() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun runFinalization() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun load(filename: String) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun loadLibrary(libname: String) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun mapLibraryName(libname: String): String {
        throw UnsupportedOperationException("not implemented")
    }
}
