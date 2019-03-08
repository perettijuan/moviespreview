package com.jpp.moviespreview.screens.main.about

sealed class Foo {
    object Foo1 : Foo()
    object Foo2 : Foo()
    object Foo3 : Foo()
    data class FooOnEsteroids(val name: String) : Foo()
}

fun iterateFoo(foo: Foo): String = when (foo) {
    is Foo.Foo1 -> "Foo1"
    is Foo.Foo2 -> "Foo2"
    is Foo.Foo3 -> "Foo3"
    is Foo.FooOnEsteroids -> foo.name
}