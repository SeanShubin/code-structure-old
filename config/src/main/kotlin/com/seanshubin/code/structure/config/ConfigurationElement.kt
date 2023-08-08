package com.seanshubin.code.structure.config

interface ConfigurationElement<T> {
    val load: () -> T
    val store: (T) -> Unit
}
