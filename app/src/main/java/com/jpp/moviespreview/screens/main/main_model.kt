package com.jpp.moviespreview.screens.main

sealed class SearchEvent {
    object ClearSearch : SearchEvent()
    data class Search(val query: String) : SearchEvent()
}