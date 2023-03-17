package com.seanshubin.code.structure.domain

interface Notifications {
    fun timeTaken(durationMilliseconds:Long)
    fun error(message:String)
}
