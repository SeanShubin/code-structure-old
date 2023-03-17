package com.seanshubin.code.structure.domain

import org.junit.Test
import kotlin.test.assertEquals

class LookupTest {
    val nullMakeLink: (Name) -> String? = { null }
    val sample = """
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
    fun simple() {
        val lines = """
            a
            b
            c
            a -> b
            b -> c
            b -> a
        """.trimIndent().split('\n')
        val actual = Lookup.fromLines(lines)
        assertEquals(listOf("a", "b", "c"), actual.names.map { it.toLine() })
        assertEquals(listOf("a->b", "b->a", "b->c"), actual.relations.map { it.toLine() })
        assertEquals(listOf("a->[b]", "b->[a c]", "c->[]"), actual.nodes.map { it.toLine() })
        assertEquals(listOf("a->[b]", "b->[a]", "c->[b]"), actual.reversedNodes.map { it.toLine() })
        assertEquals(listOf("a b"), actual.cycles.map { it.toLine() })
    }

    @Test
    fun sample() {
        // given
        val lookup = Lookup.fromLines(sample)
        val expected = """
            names
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
            relations
              a.b->c.d
              c.d->e.f.l
              e.f.k->e.f.l
              e.f.l->e.f.m
              e.f.m->e.f.n
              e.f.n->e.f.l
              e.f.n->e.f.o
              e.f.n->g.h
              g.h->c.d
              g.h->i.j
            nodes
              a->[]
              a.b->[c.d]
              c->[]
              c.d->[e.f.l]
              e->[]
              e.f->[]
              e.f.k->[e.f.l]
              e.f.l->[e.f.m]
              e.f.m->[e.f.n]
              e.f.n->[e.f.l e.f.o g.h]
              e.f.o->[]
              g->[]
              g.h->[c.d i.j]
              i->[]
              i.j->[]
            reversedNodes
              a->[]
              a.b->[]
              c->[]
              c.d->[a.b g.h]
              e->[]
              e.f->[]
              e.f.k->[]
              e.f.l->[c.d e.f.k e.f.n]
              e.f.m->[e.f.l]
              e.f.n->[e.f.m]
              e.f.o->[e.f.n]
              g->[]
              g.h->[e.f.n]
              i->[]
              i.j->[g.h]
            cycles
              c.d e.f.l e.f.m e.f.n g.h
        """.trimIndent()
        // when
        val actual = lookup.toLines().joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun children() {
        val actual = Lookup.fromLines(sample)
        actual.assertChildren("", "a", "c", "e", "g", "i")
        actual.assertChildren("a", "b")
        actual.assertChildren("a.b")
        actual.assertChildren("c", "d")
        actual.assertChildren("c.d")
        actual.assertChildren("e", "f")
        actual.assertChildren("e.f", "k", "l", "m", "n", "o")
        actual.assertChildren("e.f.k")
        actual.assertChildren("e.f.l")
        actual.assertChildren("e.f.m")
        actual.assertChildren("e.f.n")
        actual.assertChildren("e.f.o")
        actual.assertChildren("g", "h")
        actual.assertChildren("g.h")
        actual.assertChildren("i", "j")
        actual.assertChildren("i.j")
    }

    @Test
    fun dependsOn() {
        val actual = Lookup.fromLines(sample)
        actual.assertDependsOn("", "a", "c")
        actual.assertDependsOn("", "c", "e")
        actual.assertDependsOn("", "e", "g")
        actual.assertDependsOn("", "g", "c", "i")
        actual.assertDependsOn("", "i")
        actual.assertDependsOn("e.f", "k", "l")
        actual.assertDependsOn("e.f", "l", "m")
        actual.assertDependsOn("e.f", "m", "n")
        actual.assertDependsOn("e.f", "n", "l", "o")
        actual.assertDependsOn("e.f", "o")
    }

    @Test
    fun inCycle() {
        val actual = Lookup.fromLines(sample)
        actual.assertInCycle("", "a")
        actual.assertInCycle("", "c", "c", "e", "g")
        actual.assertInCycle("", "e", "c", "e", "g")
        actual.assertInCycle("", "g", "c", "e", "g")
        actual.assertInCycle("", "i")
        actual.assertInCycle("e.f", "k")
        actual.assertInCycle("e.f", "l", "l", "m", "n")
        actual.assertInCycle("e.f", "m", "l", "m", "n")
        actual.assertInCycle("e.f", "n", "l", "m", "n")
        actual.assertInCycle("e.f", "o")
    }

    @Test
    fun rootReport() {
        // given
        val lookup = Lookup.fromLines(sample)
        val expectedName = "dependencies"
        val expectedText = """
            digraph detangled {
              "a"
              "c"
              "e"
              "g"
              "i"
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
        """.trimIndent()

        // when
        val actual = lookup.report(listOf(), nullMakeLink)

        // then
        val actualText = actual.lines.joinToString("\n")
        val actualName = actual.baseName
        assertEquals(expectedText, actualText)
        assertEquals(expectedName, actualName)
    }

    @Test
    fun details() {
        val lookup = Lookup.fromLines(sample)
        val name = Name.fromString("a.b")
        assertEquals(7, lookup.depth(name))
        assertEquals(1, lookup.breadth(name))
        assertEquals(7, lookup.transitive(name))
        assertEquals(0, lookup.descendant(name))
    }

    @Test
    fun reportableContexts() {
        // given
        val lookup = Lookup.fromLines(sample)
        val expected = listOf(
            listOf(),
            listOf("a"),
            listOf("c"),
            listOf("e"),
            listOf("e", "f"),
            listOf("g"),
            listOf("i")
        )

        // when
        val actual = lookup.reportableContexts()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun generateReports() {
        // given
        val lookup = Lookup.fromLines(sample)
        val expected = """
            dependencies
            digraph detangled {
              "a" [URL="dependencies-a.svg" fontcolor=Blue]
              "c" [URL="dependencies-c.svg" fontcolor=Blue]
              "e" [URL="dependencies-e.svg" fontcolor=Blue]
              "g" [URL="dependencies-g.svg" fontcolor=Blue]
              "i" [URL="dependencies-i.svg" fontcolor=Blue]
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
            dependencies-a
            digraph detangled {
              "b"
            }
            dependencies-c
            digraph detangled {
              "d"
            }
            dependencies-e
            digraph detangled {
              "f" [URL="dependencies-e-f.svg" fontcolor=Blue]
            }
            dependencies-e-f
            digraph detangled {
              "k"
              "l"
              "m"
              "n"
              "o"
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
            dependencies-g
            digraph detangled {
              "h"
            }
            dependencies-i
            digraph detangled {
              "j"
            }
        """.trimIndent()

        // when
        val actual = lookup.generateReports().flatMap { it.toLines() }.joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun parentChild() {
        val input = """
            a -> a.b
            c.d -> c
            e.f -> e
            e -> e.f
        """.trimIndent().split("\n")
        val expected = """
            dependencies
            digraph detangled {
              "a" [URL="dependencies-a.svg" fontcolor=Blue]
              "c" [URL="dependencies-c.svg" fontcolor=Blue]
              "e" [URL="dependencies-e.svg" fontcolor=Blue]
            }
            dependencies-a
            digraph detangled {
              "b"
            }
            dependencies-c
            digraph detangled {
              "d"
            }
            dependencies-e
            digraph detangled {
              "f"
            }
        """.trimIndent()
        val lookup = Lookup.fromLines(input)

        // when
        val actual = lookup.generateReports().flatMap { it.toLines() }.joinToString("\n")

        // then
        assertEquals(expected, actual)
    }

    private fun Lookup.assertChildren(contextString: String, vararg expectedStrings: String) {
        val context = if (contextString == "") emptyList() else contextString.split(".")
        val actual = children(context)
        val expected = expectedStrings.map { Name.fromString(it) }
        assertEquals(expected, actual)
    }

    private fun Lookup.assertDependsOn(contextString: String, target: String, vararg expectedStrings: String) {
        val context = if (contextString == "") emptyList() else contextString.split(".")
        val name = Name.fromString(target)
        val actual = dependsOnNames(context, name)
        val expected = expectedStrings.map { Name.fromString(it) }
        assertEquals(expected, actual)
    }

    private fun Lookup.assertInCycle(contextString: String, target: String, vararg expectedStrings: String) {
        val context = if (contextString == "") emptyList() else contextString.split(".")
        val name = Name.fromString(target)
        val actual = namesInCycle(context, name)
        val expected = expectedStrings.map { Name.fromString(it) }
        assertEquals(expected, actual)
    }

    fun Name.toLine(): String = parts.joinToString(".")
    fun Relation.toLine(): String = "${first.toLine()}->${second.toLine()}"
    fun Node.toLine(): String {
        val nameString = name.toLine()
        val dependsOnString = dependsOn.map { it.toLine() }.joinToString(" ", "[", "]")
        return "$nameString->$dependsOnString"
    }

    fun Cycle.toLine(): String = parts.joinToString(" ") { it.toLine() }
    fun Lookup.toLines(): List<String> {
        val nameLines = names.map { it.toLine() }.map { "  $it" }
        val relationLines = relations.map { it.toLine() }.map { "  $it" }
        val nodeLines = nodes.map { it.toLine() }.map { "  $it" }
        val reversedNodeLines = reversedNodes.map { it.toLine() }.map { "  $it" }
        val cycleLines = cycles.map { it.toLine() }.map { "  $it" }
        return listOf("names") + nameLines +
                listOf("relations") + relationLines +
                listOf("nodes") + nodeLines +
                listOf("reversedNodes") + reversedNodeLines +
                listOf("cycles") + cycleLines
    }

    fun Report.toLines(): List<String> = listOf(baseName) + lines
}
