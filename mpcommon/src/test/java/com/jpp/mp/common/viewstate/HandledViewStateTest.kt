package com.jpp.mp.common.viewstate

import androidx.lifecycle.MutableLiveData
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class HandledViewStateTest {


    @Test
    fun `Should handle value only once`() {
        var sumOfHandles = 0
        val expectedHandles = 1

        val liveData = MutableLiveData<HandledViewState<Int>>()

        liveData.observeWith { state -> state.actionIfNotHandled { sumOfHandles += it } }
        liveData.observeWith { state -> state.actionIfNotHandled { sumOfHandles += it } }

        liveData.postValue(HandledViewState.of(1))

        assertEquals(expectedHandles, sumOfHandles)
    }
}