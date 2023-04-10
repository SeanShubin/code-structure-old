package com.seanshubin.code.structure.domain

import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class DetailTest {
    val sample = """
            a.b bin/a/b.binary src/a/b.source
            c.d bin/c/d.binary src/c/d.source
            g.h bin/g/h.binary src/g/h.source
            i.j bin/i/j.binary src/i/j.source
            e.f.k bin/e/f/k.binary src/e/f/k.source
            e.f.l bin/e/f/l.binary src/e/f/l.source
            e.f.m bin/e/f/m.binary src/e/f/m.source
            e.f.n bin/e/f/n.binary src/e/f/n.source
            e.f.o bin/e/f/o.binary src/e/f/o.source
            a.b -> c.d
            g.h -> c.d
            g.h -> i.j
            e.f.k -> e.f.l
            e.f.l -> e.f.m
            e.f.m -> e.f.n
            e.f.n -> e.f.l
            e.f.n -> e.f.o
            c.d -> e.f.l
            e.f.n -> g.h
        """.trimIndent().split('\n')

    @Test
    fun name() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root>
            a
            a.b
            c
            c.d
            e
            e.f
            e.f.k
            e.f.l
            e.f.m
            e.f.n
            e.f.o
            g
            g.h
            i
            i.j
        """.trimIndent()

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { it.name.toLine() }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun children() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> [a c e g i]
            a [a.b]
            a.b []
            c [c.d]
            c.d []
            e [e.f]
            e.f [e.f.k e.f.l e.f.m e.f.n e.f.o]
            e.f.k []
            e.f.l []
            e.f.m []
            e.f.n []
            e.f.o []
            g [g.h]
            g.h []
            i [i.j]
            i.j []
        """.trimIndent()

        fun childLine(detail: Detail): String {
            val name = detail.name.toLine()
            val children = detail.children.listOfDetailToLine()
            return "$name $children"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { childLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun dependsOn() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> []
            a []
            a.b [c.d]
            c []
            c.d [e.f.l]
            e []
            e.f []
            e.f.k [e.f.l]
            e.f.l [e.f.m]
            e.f.m [e.f.n]
            e.f.n [e.f.l e.f.o g.h]
            e.f.o []
            g []
            g.h [c.d i.j]
            i []
            i.j []
        """.trimIndent()

        fun dependsOnLine(detail: Detail): String {
            val name = detail.name.toLine()
            val dependsOn = detail.dependsOn.listOfDetailToLine()
            return "$name $dependsOn"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { dependsOnLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun dependedOnBy() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> []
            a []
            a.b []
            c []
            c.d [a.b g.h]
            e []
            e.f []
            e.f.k []
            e.f.l [c.d e.f.k e.f.n]
            e.f.m [e.f.l]
            e.f.n [e.f.m]
            e.f.o [e.f.n]
            g []
            g.h [e.f.n]
            i []
            i.j [g.h]
        """.trimIndent()

        fun dependedOnByLine(detail: Detail): String {
            val name = detail.name.toLine()
            val dependedOnBy = detail.dependedOnBy.listOfDetailToLine()
            return "$name $dependedOnBy"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { dependedOnByLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun cycle() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> []
            a []
            a.b []
            c []
            c.d [e.f.l e.f.m e.f.n g.h]
            e []
            e.f []
            e.f.k []
            e.f.l [c.d e.f.m e.f.n g.h]
            e.f.m [c.d e.f.l e.f.n g.h]
            e.f.n [c.d e.f.l e.f.m g.h]
            e.f.o []
            g []
            g.h [c.d e.f.l e.f.m e.f.n]
            i []
            i.j []
        """.trimIndent()

        fun cycleLine(detail: Detail): String {
            val name = detail.name.toLine()
            val cycle = detail.cycleExcludingThis.listOfDetailToLine()
            return "$name $cycle"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { cycleLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun depth() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 0
            a 0
            a.b 6
            c 0
            c.d 5
            e 0
            e.f 0
            e.f.k 6
            e.f.l 5
            e.f.m 5
            e.f.n 5
            e.f.o 0
            g 0
            g.h 5
            i 0
            i.j 0
        """.trimIndent()

        fun depthLine(detail: Detail): String {
            val name = detail.name.toLine()
            val depth = detail.depth
            return "$name $depth"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { depthLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun transitive() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 0
            a 0
            a.b 7
            c 0
            c.d 6
            e 0
            e.f 0
            e.f.k 7
            e.f.l 6
            e.f.m 6
            e.f.n 6
            e.f.o 0
            g 0
            g.h 6
            i 0
            i.j 0
        """.trimIndent()

        fun transitiveLine(detail: Detail): String {
            val name = detail.name.toLine()
            val transitive = detail.transitive
            return "$name $transitive"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { transitiveLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun aggregateChildCount() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 15
            a 1
            a.b 0
            c 1
            c.d 0
            e 6
            e.f 5
            e.f.k 0
            e.f.l 0
            e.f.m 0
            e.f.n 0
            e.f.o 0
            g 1
            g.h 0
            i 1
            i.j 0
        """.trimIndent()

        fun aggregateChildCountLine(detail: Detail): String {
            val name = detail.name.toLine()
            val aggregateChildCount = detail.aggregateChildCount()
            return "$name $aggregateChildCount"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { aggregateChildCountLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun aggregateDependsOnCount() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 0
            a 1
            a.b 1
            c 1
            c.d 1
            e 1
            e.f 1
            e.f.k 1
            e.f.l 1
            e.f.m 1
            e.f.n 3
            e.f.o 0
            g 2
            g.h 2
            i 0
            i.j 0
        """.trimIndent()

        fun aggregateDependsOnLine(detail: Detail): String {
            val name = detail.name.toLine()
            val aggregateDependsOnCount = detail.aggregateDependsOnCount()
            return "$name $aggregateDependsOnCount"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { aggregateDependsOnLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun aggregateDependedOnByCount() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 0
            a 0
            a.b 0
            c 2
            c.d 2
            e 1
            e.f 1
            e.f.k 0
            e.f.l 3
            e.f.m 1
            e.f.n 1
            e.f.o 1
            g 1
            g.h 1
            i 1
            i.j 1
        """.trimIndent()

        fun aggregateDependedOnByLine(detail: Detail): String {
            val name = detail.name.toLine()
            val aggregateDependedOnByCount = detail.aggregateDependedOnByCount()
            return "$name $aggregateDependedOnByCount"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { aggregateDependedOnByLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun aggregateDepth() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 6
            a 6
            a.b 6
            c 5
            c.d 5
            e 6
            e.f 6
            e.f.k 6
            e.f.l 5
            e.f.m 5
            e.f.n 5
            e.f.o 0
            g 5
            g.h 5
            i 0
            i.j 0
        """.trimIndent()

        fun aggregateDepthLine(detail: Detail): String {
            val name = detail.name.toLine()
            val aggregateDepth = detail.aggregateDepth()
            return "$name $aggregateDepth"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { aggregateDepthLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun aggregateTransitive() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> 7
            a 7
            a.b 7
            c 6
            c.d 6
            e 7
            e.f 7
            e.f.k 7
            e.f.l 6
            e.f.m 6
            e.f.n 6
            e.f.o 0
            g 6
            g.h 6
            i 0
            i.j 0
        """.trimIndent()

        fun aggregateTransitiveLine(detail: Detail): String {
            val name = detail.name.toLine()
            val aggregateTransitive = detail.aggregateTransitive()
            return "$name $aggregateTransitive"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { aggregateTransitiveLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun transitiveList() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> []
            a []
            a.b [c.d e.f.l e.f.m e.f.n e.f.o g.h i.j]
            c []
            c.d [e.f.l e.f.m e.f.n e.f.o g.h i.j]
            e []
            e.f []
            e.f.k [c.d e.f.l e.f.m e.f.n e.f.o g.h i.j]
            e.f.l [c.d e.f.m e.f.n e.f.o g.h i.j]
            e.f.m [c.d e.f.l e.f.n e.f.o g.h i.j]
            e.f.n [c.d e.f.l e.f.m e.f.o g.h i.j]
            e.f.o []
            g []
            g.h [c.d e.f.l e.f.m e.f.n e.f.o i.j]
            i []
            i.j []
        """.trimIndent()

        fun transitiveListLine(detail: Detail): String {
            val name = detail.name.toLine()
            val transitiveList = detail.transitiveList.listOfDetailToLine()
            return "$name $transitiveList"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { transitiveListLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun relationsNotInCycle() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> [a->c g->i]
            a []
            a.b []
            c []
            c.d []
            e []
            e.f [e.f.k->e.f.l e.f.n->e.f.o]
            e.f.k []
            e.f.l []
            e.f.m []
            e.f.n []
            e.f.o []
            g []
            g.h []
            i []
            i.j []
        """.trimIndent()

        fun relationsLine(detail: Detail): String {
            val name = detail.name.toLine()
            val relations = detail.relations().notInCycle.listOfRelationToLine()
            return "$name $relations"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { relationsLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun relationsInCycle() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            <root> [[c e g]->[c->e e->g g->c]]
            a []
            a.b []
            c []
            c.d []
            e []
            e.f [[e.f.l e.f.m e.f.n]->[e.f.l->e.f.m e.f.m->e.f.n e.f.n->e.f.l]]
            e.f.k []
            e.f.l []
            e.f.m []
            e.f.n []
            e.f.o []
            g []
            g.h []
            i []
            i.j []
        """.trimIndent()

        fun relationsNotInCycleLine(detail: Detail): String {
            val name = detail.name.toLine()
            val relationsNotInCycle = detail.relations().cycles.listOfCycleToLine()
            return "$name $relationsNotInCycle"
        }

        // when
        val actual = detail.thisAndFlattenedChildren().joinToString("\n") { relationsNotInCycleLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun generateReports() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            dependencies.txt
            digraph detangled {
              bgcolor=lightgray
              "a" [URL="dependencies-a.html" fontcolor=Blue label="a (1)"]
              "c" [URL="dependencies-c.html" fontcolor=Blue label="c (1)"]
              "e" [URL="dependencies-e.html" fontcolor=Blue label="e (6)"]
              "g" [URL="dependencies-g.html" fontcolor=Blue label="g (1)"]
              "i" [URL="dependencies-i.html" fontcolor=Blue label="i (1)"]
              "a" -> "c"
              "g" -> "i"
              subgraph cluster_0 {
                penwidth=2
                pencolor=Red
                "c" -> "e"
                "e" -> "g"
                "g" -> "c"
              }
            }
            dependencies-a.txt
            digraph detangled {
              bgcolor=lightgray
              "b" [label="b"]
            }
            dependencies-c.txt
            digraph detangled {
              bgcolor=lightgray
              "d" [label="d"]
            }
            dependencies-e.txt
            digraph detangled {
              bgcolor=lightgray
              "f" [URL="dependencies-e-f.html" fontcolor=Blue label="f (5)"]
            }
            dependencies-e-f.txt
            digraph detangled {
              bgcolor=lightgray
              "k" [label="k"]
              "l" [label="l"]
              "m" [label="m"]
              "n" [label="n"]
              "o" [label="o"]
              "k" -> "l"
              "n" -> "o"
              subgraph cluster_0 {
                penwidth=2
                pencolor=Red
                "l" -> "m"
                "m" -> "n"
                "n" -> "l"
              }
            }
            dependencies-g.txt
            digraph detangled {
              bgcolor=lightgray
              "h" [label="h"]
            }
            dependencies-i.txt
            digraph detangled {
              bgcolor=lightgray
              "j" [label="j"]
            }
        """.trimIndent()

        val simpleReportStyle: ReportStyle = SimpleReportStyle()
        val tableReportStyle: ReportStyle = TableReportStyle()
        val reportStyleMap: Map<String, ReportStyle> = mapOf(
            "simple" to simpleReportStyle,
            "table" to tableReportStyle
        )
        val reportFormat: ReportFormat = DotReportFormat(reportStyleMap)

        fun reportLines(detail: Detail): List<String> {
            val reportDir = Paths.get("should-not-exist")
            val report = reportFormat.report(reportDir, detail, "simple") ?: return emptyList()
            val baseName = report.name
            val dotLines = report.lines
            return listOf(baseName) + dotLines
        }

        // when
        val actual = detail.thisAndFlattenedChildren().flatMap { reportLines(it) }.joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun cycles() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
            [c.d e.f.l e.f.m e.f.n g.h]
        """.trimIndent()

        // when
        val actual = detail.findAllCycles().joinToString("\n") { it.listOfDetailToLine() }

        // then
        assertEquals(expected, actual)
    }

    private fun Name.toLine(): String = if (parts.isEmpty()) "<root>" else parts.joinToString(".")
    private fun Relation.toLine(): String = "${first.toLine()}->${second.toLine()}"
    private fun List<Relation>.listOfRelationToLine(): String = joinToString(" ", "[", "]") { it.toLine() }
    private fun Detail.toLine() = name.toLine()
    private fun List<Detail>.listOfDetailToLine(): String = joinToString(" ", "[", "]") { it.toLine() }
    private fun List<Name>.listOfNameToLine(): String = joinToString(" ", "[", "]") { it.toLine() }
    private fun Pair<List<Name>, List<Relation>>.cycleToLine(): String {
        val nameLine = first.listOfNameToLine()
        val relationLine = second.listOfRelationToLine()
        return "$nameLine->$relationLine"
    }

    private fun List<Pair<List<Name>, List<Relation>>>.listOfCycleToLine(): String =
        joinToString(" ", "[", "]") { it.cycleToLine() }

}
