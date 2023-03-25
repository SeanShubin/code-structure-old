package com.seanshubin.code.structure.console

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        DependenciesWithArgs(args).runner.run()
    }
}
