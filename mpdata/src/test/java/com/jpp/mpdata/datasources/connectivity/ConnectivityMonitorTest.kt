package com.jpp.mpdata.datasources.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConnectivityMonitorTest {

    @MockK
    private lateinit var context: Context
    @RelaxedMockK
    private lateinit var connectivityManager: ConnectivityManager

    @BeforeEach
    fun setUp() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @Test
    fun `ConnectivityMonitorAPI24 should register network callback on startMonitoring`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI24(context)

        subject.startMonitoring { }

        verify { connectivityManager.registerDefaultNetworkCallback(any()) }
    }

    @Test
    fun `ConnectivityMonitorAPI24 should unregister network callback on stopMonitoring`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI24(context)

        subject.stopMonitoring()

        verify { connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>()) }
    }

    @Test
    fun `ConnectivityMonitorAPI24 should invoke lambda when connectivity available`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI24(context)
        val slot = slot<ConnectivityManager.NetworkCallback>()
        val lambdaMock = mockk<() -> Unit>(relaxed = true)

        every { connectivityManager.registerDefaultNetworkCallback(capture(slot)) } answers { Unit }

        subject.startMonitoring(lambdaMock)

        slot.captured.onAvailable(mockk())

        verify { lambdaMock.invoke() }
    }

    @Test
    fun `ConnectivityMonitorAPI24 should invoke lambda when connectivity lost`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI24(context)
        val slot = slot<ConnectivityManager.NetworkCallback>()
        val lambdaMock = mockk<() -> Unit>(relaxed = true)

        every { connectivityManager.registerDefaultNetworkCallback(capture(slot)) } answers { Unit }

        subject.startMonitoring(lambdaMock)

        slot.captured.onLost(mockk())

        verify { lambdaMock.invoke() }
    }

    @Test
    fun `ConnectivityMonitorAPI23 should register broadcast receiver on startMonitoring`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI23(context)

        every { context.registerReceiver(any(), any()) } answers { mockk() }

        subject.startMonitoring {  }

        verify { context.registerReceiver(any(), any()) }
    }

    @Test
    fun `ConnectivityMonitorAPI23 should unregister broadcast receiver on startMonitoring`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI23(context)
        val slot = slot<BroadcastReceiver>()

        every { context.registerReceiver(capture(slot), any()) } answers { mockk() }
        every { context.unregisterReceiver(any()) } just Runs

        subject.startMonitoring {  }
        subject.stopMonitoring()

        verify { context.unregisterReceiver(slot.captured) }
    }

    @Test
    fun `ConnectivityMonitorAPI23 should invoke lambda when connectivity changed`() {
        val subject: ConnectivityMonitor = ConnectivityMonitor.ConnectivityMonitorAPI23(context)
        val slot = slot<BroadcastReceiver>()
        val lambdaMock = mockk<() -> Unit>(relaxed = true)

        every { context.registerReceiver(capture(slot), any()) } answers { mockk() }

        subject.startMonitoring(lambdaMock)
        slot.captured.onReceive(mockk(), mockk())

        verify { lambdaMock.invoke() }
    }
}