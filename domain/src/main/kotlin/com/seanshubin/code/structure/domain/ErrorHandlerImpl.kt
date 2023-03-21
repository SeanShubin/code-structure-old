package com.seanshubin.code.structure.domain

class ErrorHandlerImpl(
    private val errorEvent: (String) -> Unit,
    private val exit: (Int) -> Nothing
) : ErrorHandler {
    override fun error(message: String): Nothing {
        errorEvent(message)
        exit(1)
    }
}
