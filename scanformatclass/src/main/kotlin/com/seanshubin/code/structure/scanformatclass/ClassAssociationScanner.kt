package com.seanshubin.code.structure.scanformatclass

import com.seanshubin.code.structure.scanformat.AssociationScanner
import com.seanshubin.code.structure.scanformat.AssociationsRepository
import com.seanshubin.code.structure.scanformat.DependencyLoader
import com.seanshubin.code.structure.scanformat.DependencyModule

class ClassAssociationScanner(
    private val dependencyLoader: DependencyLoader,
    private val associationsRepository: AssociationsRepository,
    private val summarize: () -> Unit
) : AssociationScanner {
    override fun scanAssociations() {
        val modules = dependencyLoader.loadModules()
        val filteredModules1 = modules.filter {
            it.source != null && it.binary != null
        }
        val filteredModules2 = filteredModules1.filterExternalDependencies()
        associationsRepository.storeAssociations(filteredModules2)
        summarize()
    }

    private fun List<DependencyModule>.filterExternalDependencies(): List<DependencyModule> {
        val moduleByName = associateBy { it.name }
        return map { oldModule ->
            val newDependencies = oldModule.dependencies.filter(moduleByName::containsKey)
            oldModule.copy(dependencies = newDependencies)
        }
    }
}