package com.seanshubin.code.structure.domain

interface ErrorHandler {
    fun error(message: String): Nothing
}
