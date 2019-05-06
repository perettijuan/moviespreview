package com.jpp.mpdata.repository.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.utiltest.observeWith
import com.jpp.mpdata.datasources.connectivity.ConnectivityMonitor
import com.jpp.mpdomain.Connectivity
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MPConnectivityRepositoryTest {

    @RelaxedMockK
    private lateinit var monitor: ConnectivityMonitor

    @MockK
    private lateinit var connectivityManager: ConnectivityManager

    @MockK
    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @ParameterizedTest
    @MethodSource("data")
    fun `Should notify connectivity event with proper connectivity state`(connected: Boolean, expected: Connectivity) {
        val slot = slot<() -> Unit>()
        val networkInfo = mockk<NetworkInfo>()
        var actual: Connectivity? = null

        every { monitor.addListener(capture(slot)) } answers { slot.captured.invoke() }
        every { connectivityManager.activeNetworkInfo } returns networkInfo
        every { networkInfo.isConnected } returns connected

        val subject = MPConnectivityRepositoryImpl(monitor, context)
        subject.data().observeWith { update -> actual = update }

        assertEquals(expected, actual)
    }

    companion object {

        @JvmStatic
        fun connectivityUpdates() = listOf(
                Arguments.of(true, Connectivity.Connected),
                Arguments.of(false, Connectivity.Disconnected)
        )
    }
}