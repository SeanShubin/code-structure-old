package com.seanshubin.code.structure.scanformatclass

import java.io.DataInput

class ClassParserImpl : ClassParser {
    override fun parseClassDependencies(dataInput: DataInput): Pair<String, List<String>> {
        val classFileInfo = ClassFileInfo.fromDataInput(dataInput)
        val thisClassName = classFileInfo.thisClassName.formatClassName()
        val dependencyNames = classFileInfo.dependencyNames.map { it.formatClassName() }
        val dependencies = Pair(thisClassName, dependencyNames)
        return dependencies
    }

    private fun String.formatClassName(): String {
        val dollarIndex = this.indexOf('$')
        val dollarRemoved = if (dollarIndex == -1) this else {
            this.substring(0, dollarIndex)
        }
        return dollarRemoved.replace('/', '.')
    }
}
