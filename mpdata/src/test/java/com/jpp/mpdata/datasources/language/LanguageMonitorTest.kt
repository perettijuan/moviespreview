package com.jpp.mpdata.datasources.language

import android.content.BroadcastReceiver
import android.content.Context
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LanguageMonitorTest {

    @RelaxedMockK
    private lateinit var context: Context

    private lateinit var subject: LanguageMonitor

    @BeforeEach
    fun setUp() {
        subject = LanguageMonitor.Impl(context)
    }

    @Disabled("Flaky test in UI")
    @Test
    fun `Should invoke lambda when language changed`() {
        val bcReceiverSlot = slot<BroadcastReceiver>()
        val lambdaMock = mockk<() -> Unit>(relaxed = true)

        every { context.registerReceiver(capture(bcReceiverSlot), any()) } answers { mockk() }

        subject.startMonitoring()
        subject.addListener(lambdaMock)

        bcReceiverSlot.captured.onReceive(mockk(), mockk())

        verify { lambdaMock.invoke() }
    }

    @Disabled("Flaky test in UI")
    @Test
    fun `Should clear listeners in stopMonitoring`() {
        val bcReceiverSlot = slot<BroadcastReceiver>()
        val lambdaMock = mockk<() -> Unit>(relaxed = true)

        every { context.registerReceiver(capture(bcReceiverSlot), any()) } answers { mockk() }

        subject.startMonitoring()
        subject.addListener(lambdaMock)

        bcReceiverSlot.captured.onReceive(mockk(), mockk())

        // stop
        subject.stopMonitoring()
        bcReceiverSlot.captured.onReceive(mockk(), mockk())

        verify(exactly = 1) { lambdaMock.invoke() }
    }
}
