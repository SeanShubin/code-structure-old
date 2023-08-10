package com.seanshubin.code.structure.scanformat

interface AssociationsRepository {
    fun loadAssociations(): List<DependencyModule>
    fun storeAssociations(associations: List<DependencyModule>)
}
