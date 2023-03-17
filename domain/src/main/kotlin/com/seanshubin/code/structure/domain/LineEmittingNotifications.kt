package com.seanshubin.code.structure.domain

class LineEmittingNotifications(
    private val emitLine:(String)->Unit
):Notifications {
    override fun timeTaken(durationMilliseconds: Long) {
        val formattedDuration = DurationFormat.milliseconds.format(durationMilliseconds)
        emitLine(formattedDuration)
    }

    override fun error(message: String) {
        emitLine(message)
    }
}