package com.jpp.moviespreview.screens.main

import androidx.lifecycle.Observer
import com.jpp.moviespreview.InstantTaskExecutorExtension
import com.jpp.moviespreview.resumedLifecycleOwner
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class MainActivityViewModelTest {

    private val subject = MainActivityViewModel()

    @Test
    fun `Should lock ActionBar without animation when user navigates to movies list`() {
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals("aSection", abTitle)
                assertFalse(withAnimation)
            }
        })
        subject.userNavigatesToMovieListSection("aSection")
    }

    @Test
    fun `Should lock ActionBar with animation when user navigates to movies list back from details`() {
        val viewStatesPosted = mutableListOf<MainActivityViewState>()
        // navigate to details to pre-set the ActionBar state as unlocked
        subject.userNavigatesToMovieDetails(movieTitle = "aTitle", contentImageUrl = "aUrl")

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatesPosted.add(it)
        })

        subject.userNavigatesToMovieListSection("aSection")

        assertTrue(viewStatesPosted[1] is MainActivityViewState.ActionBarLocked)
        with(viewStatesPosted[1] as MainActivityViewState.ActionBarLocked) {
            assertEquals("aSection", abTitle)
            assertTrue(withAnimation)
        }
    }

    @Test
    fun `Should unlock ActionBar when user navigates to details`() {
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarUnlocked)
            with(it as MainActivityViewState.ActionBarUnlocked) {
                assertEquals("aTitle", abTitle)
                assertEquals("aUrl", contentImageUrl)
            }
        })
        subject.userNavigatesToMovieDetails(movieTitle = "aTitle", contentImageUrl = "aUrl")
    }

    @Test
    fun `Should navigate to search section without ActionBar animation when user opens search`() {
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.SearchEnabled)
            with(it as MainActivityViewState.SearchEnabled) {
                assertFalse(withAnimation)
            }
        })
        subject.userNavigatesToSearch()
    }

    @Test
    fun `Should navigate to search section with ActionBar animation when user navigates to search back from details`() {
        val viewStatesPosted = mutableListOf<MainActivityViewState>()
        // navigate to details to pre-set the ActionBar state as unlocked
        subject.userNavigatesToMovieDetails(movieTitle = "aTitle", contentImageUrl = "aUrl")

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatesPosted.add(it)
        })

        subject.userNavigatesToSearch()

        assertTrue(viewStatesPosted[1] is MainActivityViewState.SearchEnabled)
        with(viewStatesPosted[1] as MainActivityViewState.SearchEnabled) {
            assertTrue(withAnimation)
        }
    }

}