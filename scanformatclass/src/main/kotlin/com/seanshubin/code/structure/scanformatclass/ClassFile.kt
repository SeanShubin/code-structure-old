package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.scanformat.ModuleFile

class ClassFile(
    override val name: String,
    override val dependencies: List<String>
) : ModuleFile
