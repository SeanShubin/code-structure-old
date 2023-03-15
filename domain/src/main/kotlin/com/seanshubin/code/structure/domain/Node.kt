package com.seanshubin.code.structure.domain

data class Node(val name:Name, val dependsOn:List<Name>) {
    fun startsWith(context:List<String>):Boolean = name.startsWith(context)
    fun startsWith(target:Name):Boolean = name.startsWith(target)
    val simpleString:String get() {
        val nameString = name.simpleString
        val dependsOnString = dependsOn.map{it.simpleString}.joinToString(" ", "[", "]")
        return "$nameString->$dependsOnString"
    }
}
