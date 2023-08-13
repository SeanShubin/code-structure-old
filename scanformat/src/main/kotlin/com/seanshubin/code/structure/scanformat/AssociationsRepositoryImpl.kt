package com.seanshubin.code.structure.scanformat

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.datatypes.NameBinarySourceDto
import com.seanshubin.code.structure.datatypes.RelationDto
import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class AssociationsRepositoryImpl(
    private val path: Path,
    private val files: FilesContract
) : AssociationsRepository {
    override fun loadAssociations(): List<DependencyModule> {
        val lines = files.readAllLines(path)
        val names = mutableListOf<NameBinarySourceDto>()
        val relations = mutableListOf<RelationDto>()
        lines.windowed(2, 2).forEach {
            val typeName = it[0]
            val line = it[1]
            when (typeName) {
                "name" -> {
                    names.add(JsonMappers.parser.readValue<NameBinarySourceDto>(line))
                }
                "relation" -> {
                    relations.add(JsonMappers.parser.readValue<RelationDto>(line))
                }
                else -> {
                    throw RuntimeException("Unsupported type '$typeName'")
                }
            }
        }
        val relationByFirst = relations.groupBy { it.first }
        val dependencyModules = names.map { nameBinarySourceDto ->
            val name = nameBinarySourceDto.name
            val dependencies = (relationByFirst[name] ?: emptyList()).map { relationDto ->
                relationDto.second
            }
            val binary = nameBinarySourceDto.binary
            val source = nameBinarySourceDto.source
            DependencyModule(name, dependencies, binary, source)
        }
        return dependencyModules
    }

    override fun storeAssociations(associations: List<DependencyModule>) {
        val singleLines = associations.map { dependencyModule ->
            NameBinarySourceDto(dependencyModule.name, dependencyModule.binary, dependencyModule.source)
        }.flatMap { listOf("name", JsonMappers.compact.writeValueAsString(it)) }
        val relationLines = associations.flatMap { dependencyModule ->
            dependencyModule.dependencies.map { dependency ->
                RelationDto(dependencyModule.name, dependency)
            }
        }.flatMap { listOf("relation", JsonMappers.compact.writeValueAsString(it)) }
        val lines = singleLines + relationLines
        path.ensureParentExists(files)
        files.write(path, lines)
    }

    private fun Path.ensureParentExists(files: FilesContract) {
        val parent = this.parent
        if (parent != null) {
            files.createDirectories(parent)
        }
    }
}
