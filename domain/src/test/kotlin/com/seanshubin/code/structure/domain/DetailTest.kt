package com.seanshubin.code.structure.domain

import org.junit.Test
import kotlin.test.assertEquals

class DetailTest {
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
    fun name(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        val actual = detail.flattenChildren().joinToString("\n") { it.name.toLine() }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun children() {
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
            val children = detail.children().toLine()
            return "$name $children"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { childLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun dependsOn(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        fun dependsOnLine(detail:Detail):String {
            val name = detail.name.toLine()
            val dependsOn = detail.dependsOn().toLine()
            return "$name $dependsOn"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { dependsOnLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun cycle(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        fun cycleLine(detail:Detail):String {
            val name = detail.name.toLine()
            val cycle = detail.cycleExcludingThis().toLine()
            return "$name $cycle"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { cycleLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun depth(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        fun depthLine(detail:Detail):String {
            val name = detail.name.toLine()
            val depth = detail.depth
            return "$name $depth"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { depthLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun transitive(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        fun transitiveLine(detail:Detail):String {
            val name = detail.name.toLine()
            val transitive = detail.transitive
            return "$name $transitive"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { transitiveLine(it) }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun transitiveList(){
        // given
        val detail = DetailBuilder.fromLines(sample)
        val expected = """
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
        fun transitiveListLine(detail:Detail):String {
            val name = detail.name.toLine()
            val transitiveList = detail.transitiveList.toLine()
            return "$name $transitiveList"
        }

        // when
        val actual = detail.flattenChildren().joinToString("\n") { transitiveListLine(it) }

        // then
        assertEquals(expected, actual)
    }

    private fun Name.toLine():String = parts.joinToString(".")
    private fun List<Detail>.toLine():String = joinToString(" ", "[", "]") { it.name.toLine() }
}
