package com.jpp.mp.screens.main.about

import com.jpp.mptestutils.InstantTaskExecutorExtension
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AboutViewModelDeprecatedTest {


    private lateinit var subject: AboutViewModelDeprecated

    @BeforeEach
    fun setUp() {
        subject = AboutViewModelDeprecated()
    }


    companion object {


    }
}