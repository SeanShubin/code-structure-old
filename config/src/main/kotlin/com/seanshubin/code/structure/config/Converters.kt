package com.seanshubin.code.structure.config

import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

object Converters {
    object StringConverter : Converter<String> {
        override val sourceType: Class<*> get() = String::class.java

        override fun convert(value: Any?): String? =
            if (value is String) value else null
    }

    object IntConverter : Converter<Int> {
        override val sourceType: Class<*> get() = Int::class.java

        override fun convert(value: Any?): Int? =
            if (value is Int) value else null
    }

    object PathConverter : Converter<Path> {
        override val sourceType: Class<*> get() = String::class.java

        override fun convert(value: Any?): Path? =
            if (value is String) Paths.get(value) else null
    }

    object InstantConverter : Converter<Instant> {
        override val sourceType: Class<*> get() = Instant::class.java

        override fun convert(value: Any?): Instant? =
            if (value is String) Instant.parse(value) else null
    }

    object StringListConverter : Converter<List<String>> {
        override val sourceType: Class<*> = List::class.java

        override fun convert(value: Any?): List<String>? {
            if (value !is List<*>) return null
            return value.map {
                when (it) {
                    is String -> it
                    else -> return null
                }
            }
        }
    }

    object PathListConverter : Converter<List<Path>> {
        override val sourceType: Class<*> = List::class.java

        override fun convert(value: Any?): List<Path>? {
            if (value !is List<*>) return null
            return value.map {
                when (it) {
                    is String -> Paths.get(it)
                    is Path -> it
                    else -> return null
                }
            }
        }
    }
}
