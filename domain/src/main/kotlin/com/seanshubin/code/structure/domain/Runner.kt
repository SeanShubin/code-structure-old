package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.SystemContract
import com.seanshubin.code.structure.datatypes.NameBinarySource
import com.seanshubin.code.structure.datatypes.NameDto.Companion.toName
import com.seanshubin.code.structure.datatypes.Relation
import com.seanshubin.code.structure.scanformat.AssociationScanner
import com.seanshubin.code.structure.scanformat.AssociationsRepository
import com.seanshubin.code.structure.scanformat.DependencyModule

class Runner(
    private val associationScanner: AssociationScanner,
    private val reportGenerator: ReportGenerator,
    private val system: SystemContract,
    private val associationsRepository: AssociationsRepository,
    private val timeTaken: (Long) -> Unit
) : Runnable {
    override fun run() {
        val startTime = system.currentTimeMillis()
        runMeInsideTimer()
        val endTime = system.currentTimeMillis()
        val duration = endTime - startTime
        timeTaken(duration)
    }

    private fun runMeInsideTimer() {
        associationScanner.scanAssociations()
        val associations = associationsRepository.loadAssociations()
        val (names, relations) = associations.toNamesAndRelations()
        val detail = DetailBuilder.fromNamesAndRelations(names, relations)
        reportGenerator.generateReports(detail)
    }

    private fun List<DependencyModule>.toNamesAndRelations(): Pair<List<NameBinarySource>, List<Relation>> {
        val pairs = this.map { it.toNameAndDependencies() }
        val names = pairs.map { it.first }
        val relations = pairs.flatMap { it.second }
        return Pair(names, relations)
    }

    private fun DependencyModule.toNameAndDependencies(): Pair<NameBinarySource, List<Relation>> {
        val name = NameBinarySource(this.name.toName(), this.binary, this.source)
        val relations = this.dependencies.map {
            Relation(name.name, it.toName())
        }
        return Pair(name, relations)
    }
}
