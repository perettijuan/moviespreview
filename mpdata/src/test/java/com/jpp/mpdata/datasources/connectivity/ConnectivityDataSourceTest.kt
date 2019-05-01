package com.jpp.mpdata.datasources.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.utiltest.observeWith
import com.jpp.mpdomain.Connectivity
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class ConnectivityDataSourceTest {

    @RelaxedMockK
    private lateinit var monitor: ConnectivityMonitor

    @MockK
    private lateinit var connectivityManager: ConnectivityManager

    @MockK
    private lateinit var context: Context

    private lateinit var subject: ConnectivityDataSource

    @BeforeEach
    fun setUp() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        subject = ConnectivityDataSource.Impl(monitor, context)
    }

    @Test
    fun `Should start monitoring when enableUpdates is called`() {
        subject.enableUpdates()

        verify { monitor.startMonitoring(any()) }
    }

    @Test
    fun `Should stop monitoring when disableUpdates is called`() {
        subject.disableUpdates()

        verify { monitor.stopMonitoring() }
    }

    @ParameterizedTest
    @MethodSource("connectivityUpdates")
    fun `Should notify connectivity event with proper connectivity state`(connected: Boolean, expected: Connectivity) {
        val slot = slot<() -> Unit>()
        val networkInfo = mockk<NetworkInfo>()
        var actual: Connectivity? = null

        every { monitor.startMonitoring(capture(slot)) } answers { slot.captured.invoke() }
        every { connectivityManager.activeNetworkInfo } returns networkInfo
        every { networkInfo.isConnected } returns connected

        subject.connectivityUpdates().observeWith { update -> actual = update }
        subject.enableUpdates()

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