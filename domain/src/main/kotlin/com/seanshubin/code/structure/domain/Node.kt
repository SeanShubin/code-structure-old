package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.datatypes.Name

data class Node(val name: Name, val dependsOn: List<Name>)
