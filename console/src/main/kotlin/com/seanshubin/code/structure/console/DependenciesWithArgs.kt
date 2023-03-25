package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.domain.*

class DependenciesWithArgs(args: Array<String>) {
    private val createRunnable:(CodeStructureAppConfig) -> Runnable = { config ->
        DependenciesWithConfig(config).runner
    }
    val runner: Runnable = RunnerWithArgs(
        args,
        createRunnable
    )
}
