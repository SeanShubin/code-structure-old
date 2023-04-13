package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.domain.NameComposer.svgFileName
import java.nio.file.Path

class SvgLoader(
    private val files:FilesContract
):(Path, String)->List<String> {
    override fun invoke(reportDir: Path, baseName:String): List<String> {
        val svgFileName = "$baseName.svg"
        val svgPath = reportDir.resolve(svgFileName)
        return files.readAllLines(svgPath)
    }
}
