package com.jpp.mp.screens.main

import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.utiltest.resumedLifecycleOwner
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
                assertTrue(menuEnabled)
                assertFalse(searchEnabled)
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
            assertTrue(menuEnabled)
            assertFalse(searchEnabled)
        }
    }

    @Test
    fun `Should unlock ActionBar when user navigates to details`() {
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarUnlocked)
            with(it as MainActivityViewState.ActionBarUnlocked) {
                assertEquals("aTitle", abTitle)
                assertEquals("aUrl", contentImageUrl)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToMovieDetails(movieTitle = "aTitle", contentImageUrl = "aUrl")
    }

    @Test
    fun `Should navigate to search section without ActionBar animation when user opens search`() {
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertFalse(withAnimation)
                assertTrue(searchEnabled)
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

        assertTrue(viewStatesPosted[1] is MainActivityViewState.ActionBarLocked)
        with(viewStatesPosted[1] as MainActivityViewState.ActionBarLocked) {
            assertTrue(withAnimation)
            assertTrue(searchEnabled)
        }
    }

    @Test
    fun `Should lock ActionBar without animation when user navigates to movies person`() {
        val personName = "aPerson"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals(personName, abTitle)
                assertFalse(withAnimation)
                assertFalse(menuEnabled)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToPerson(personName)
    }


    @Test
    fun `Should lock ActionBar with animation when user navigates to movies credits`() {
        val viewStatesPosted = mutableListOf<MainActivityViewState>()
        // navigate to details to pre-set the ActionBar state as unlocked
        subject.userNavigatesToMovieDetails(movieTitle = "aTitle", contentImageUrl = "aUrl")

        val creditsName = "credits"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatesPosted.add(it)
        })
        subject.userNavigatesToCredits(creditsName)

        assertTrue(viewStatesPosted[1] is MainActivityViewState.ActionBarLocked)
        with(viewStatesPosted[1] as MainActivityViewState.ActionBarLocked) {
            assertEquals(creditsName, abTitle)
            assertTrue(withAnimation)
            assertFalse(menuEnabled)
            assertFalse(searchEnabled)
        }
    }

    @Test
    fun `Should lock ActionBar without animation when user navigates to movies about`() {
        val sectionName = "aSection"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals(sectionName, abTitle)
                assertFalse(withAnimation)
                assertFalse(menuEnabled)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToAbout(sectionName)
    }

    @Test
    fun `Should lock ActionBar without animation when user navigates to movies licenses`() {
        val sectionName = "aSection"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals(sectionName, abTitle)
                assertFalse(withAnimation)
                assertFalse(menuEnabled)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToLicenses(sectionName)
    }

    @Test
    fun `Should lock ActionBar without animation when user navigates to movies licenses content`() {
        val sectionName = "aSection"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals(sectionName, abTitle)
                assertFalse(withAnimation)
                assertFalse(menuEnabled)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToLicenseContent(sectionName)
    }

    @Test
    fun `Should lock ActionBar without animation when user navigates to account details`() {
        val sectionName = "aSection"
        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is MainActivityViewState.ActionBarLocked)
            with(it as MainActivityViewState.ActionBarLocked) {
                assertEquals(sectionName, abTitle)
                assertFalse(withAnimation)
                assertFalse(menuEnabled)
                assertFalse(searchEnabled)
            }
        })
        subject.userNavigatesToAccountDetails(sectionName)
    }

}