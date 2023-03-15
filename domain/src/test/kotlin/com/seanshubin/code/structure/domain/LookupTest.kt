package com.seanshubin.code.structure.domain

import org.junit.Test
import kotlin.test.assertEquals

class LookupTest {
    val sample = """
            a.b -> c.d
            c.d -> e.f
            e.f -> g.h
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
        assertEquals(listOf("a", "b", "c"), actual.names.map { it.simpleString })
        assertEquals(listOf("a->b", "b->a", "b->c"), actual.relations.map { it.simpleString })
        assertEquals(listOf("a->[b]", "b->[a c]"), actual.nodes.map { it.simpleString })
        assertEquals(listOf("b->[a]", "a->[b]", "c->[b]"), actual.reversedNodes.map { it.simpleString })
        assertEquals(listOf("a b"), actual.cycles.map { it.simpleString })
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
              c.d->e.f
              c.d->e.f.l
              e.f->g.h
              e.f.k->e.f.l
              e.f.l->e.f.m
              e.f.m->e.f.n
              e.f.n->e.f.l
              e.f.n->e.f.o
              e.f.n->g.h
              g.h->c.d
              g.h->i.j
            nodes
              a.b->[c.d]
              c.d->[e.f e.f.l]
              e.f->[g.h]
              e.f.k->[e.f.l]
              e.f.l->[e.f.m]
              e.f.m->[e.f.n]
              e.f.n->[e.f.l e.f.o g.h]
              g.h->[c.d i.j]
            reversedNodes
              c.d->[a.b g.h]
              e.f->[c.d]
              e.f.l->[c.d e.f.k e.f.n]
              g.h->[e.f e.f.n]
              e.f.m->[e.f.l]
              e.f.n->[e.f.m]
              e.f.o->[e.f.n]
              i.j->[g.h]
            cycles
              c.d e.f e.f.l e.f.m e.f.n g.h
        """.trimIndent().split("\n")
        // when
        val actual = lookup.toLines()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun children() {
        val actual = Lookup.fromLines(sample)
        actual.assertChildren("", "a", "c", "e", "g", "i")
        actual.assertChildren("a", "a.b")
        actual.assertChildren("a.b")
        actual.assertChildren("c", "c.d")
        actual.assertChildren("c.d")
        actual.assertChildren("e", "e.f")
        actual.assertChildren("e.f", "e.f.k", "e.f.l", "e.f.m", "e.f.n", "e.f.o")
        actual.assertChildren("e.f.k")
        actual.assertChildren("e.f.l")
        actual.assertChildren("e.f.m")
        actual.assertChildren("e.f.n")
        actual.assertChildren("e.f.o")
        actual.assertChildren("g", "g.h")
        actual.assertChildren("g.h")
        actual.assertChildren("i", "i.j")
        actual.assertChildren("i.j")
    }

    @Test
    fun dependsOn() {
        val actual = Lookup.fromLines(sample)
        actual.assertDependsOn("","a", "c")
        actual.assertDependsOn("","c", "e")
        actual.assertDependsOn("","e", "g")
        actual.assertDependsOn("","g", "c", "i")
        actual.assertDependsOn("","i")
    }

    private fun Lookup.assertChildren(contextString:String, vararg expectedStrings: String) {
        val context = if (contextString == "") emptyList() else contextString.split(".")
        val expected = expectedStrings.map { Name.fromString(it) }
        val actual = children(context)
        assertEquals(expected, actual)
    }

    private fun Lookup.assertDependsOn(contextString: String, nameString:String, vararg expectedStrings: String) {
        val context = if (contextString == "") emptyList() else contextString.split(".")
        val name = Name.fromString(nameString)
        val expected = expectedStrings.map { Name.fromString(it) }
        val actual = dependsOn(context, name)
        assertEquals(expected, actual)
    }
}
