package com.seanshubin.code.structure.scanformat

interface DependencyLoader {
    fun loadModules(): List<DependencyModule>
}
