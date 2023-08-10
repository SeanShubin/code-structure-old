package com.seanshubin.code.structure.console

import java.time.Clock

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        DependenciesWithArgs(args, Clock.systemUTC().instant()).runner.run()
    }
}
