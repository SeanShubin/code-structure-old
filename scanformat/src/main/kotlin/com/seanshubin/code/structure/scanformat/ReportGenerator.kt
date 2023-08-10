package com.seanshubin.code.structure.scanformat

interface ReportGenerator {
    fun generate(modules: List<DependencyModule>)
}
