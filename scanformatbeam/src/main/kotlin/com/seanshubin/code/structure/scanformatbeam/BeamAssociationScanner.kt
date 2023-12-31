package com.seanshubin.code.structure.scanformatbeam

import com.seanshubin.code.structure.scanformat.AssociationScanner
import com.seanshubin.code.structure.scanformat.AssociationsRepository
import com.seanshubin.code.structure.scanformat.DependencyLoader
import com.seanshubin.code.structure.scanformat.DependencyModule

class BeamAssociationScanner(
    private val dependencyLoader: DependencyLoader,
    private val associationsRepository: AssociationsRepository,
    private val summarize: () -> Unit
) : AssociationScanner {
    override fun scanAssociations() {
        val modules = dependencyLoader.loadModules().filter {
            it.source != null && it.binary != null
        }.filterExternalDependencies()
        associationsRepository.storeAssociations(modules)
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
