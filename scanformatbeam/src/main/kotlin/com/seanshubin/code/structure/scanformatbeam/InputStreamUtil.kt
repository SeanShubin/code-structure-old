package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.scanformatbeam.Conversions.toInt
import java.io.InputStream

object InputStreamUtil {
    fun InputStream.consumeStringLiteral(s: String) {
        var index = 0
        while (index < s.length) {
            val actualInt = read()
            val expectedChar = s[index]
            if (actualInt == -1) {
                throw RuntimeException("Expected '$expectedChar', got end of file")
            }
            val actualChar = actualInt.toChar()
            if (expectedChar != actualChar) {
                throw RuntimeException("Expected '$expectedChar', got '$actualChar'")
            }
            index++
        }
    }

    fun InputStream.consumeStringOfSizeOrNull(size: Int): String? {
        var index = 0
        val chars = mutableListOf<Char>()
        while (index < size) {
            val actualInt = read()
            if (actualInt == -1) {
                return null
            }
            val actualChar = actualInt.toChar()
            chars.add(actualChar)
            index++
        }
        return chars.joinToString("")
    }

    fun InputStream.consumeStringOfSize(size: Int): String {
        var index = 0
        val chars = mutableListOf<Char>()
        while (index < size) {
            val actualInt = read()
            if (actualInt == -1) {
                throw RuntimeException("Expected string of size $size, got end of file")
            }
            val actualChar = actualInt.toChar()
            chars.add(actualChar)
            index++
        }
        return chars.joinToString("")
    }

    fun InputStream.consumeInt(): Int {
        var index = 0
        val byteList = mutableListOf<Byte>()
        while (index < 4) {
            val byteAsInt = read()
            if (byteAsInt == -1) {
                throw RuntimeException("Expecting 4 bytes, got end of file")
            }
            byteList.add(byteAsInt.toByte())
            index++
        }
        return byteList.toInt()
    }

    fun InputStream.consumeBytes(size: Int): List<Byte> {
        var index = 0
        val byteList = mutableListOf<Byte>()
        while (index < size) {
            val byteAsInt = read()
            if (byteAsInt == -1) {
                throw RuntimeException("Expecting $size bytes, got end of file after $index")
            }
            byteList.add(byteAsInt.toByte())
            index++
        }
        while (index % 4 != 0) {
            read()
            index++
        }
        return byteList
    }
}