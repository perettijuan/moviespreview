package com.jpp.mp.common.navigation

import android.view.View
import androidx.navigation.NavDirections
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class NavigationViewModelTest {

    private lateinit var subject: NavigationViewModel

    @BeforeEach
    fun setUp() {
        subject = NavigationViewModel()
    }

    @ParameterizedTest
    @MethodSource("destinationTestParams")
    fun `Should post Destination when`(param: NavigationViewModelTestParam) {
        var actual: Destination? = null

        subject.navEvents.observeWith { it.actionIfNotHandled { state -> actual = state } }

        param.whenAction.invoke(subject)

        assertEquals(param.expected, actual)
    }

    /*
     * Really nice way of parametrize a test with a lambda call.
     */
    data class NavigationViewModelTestParam(
            val whenAction: (NavigationViewModel) -> Unit,
            val expected: Destination
    )


    companion object {

        private val transitionViewMock = mockk<View>()
        private val navDirectionsMock = mockk<NavDirections>()

        @JvmStatic
        fun destinationTestParams() = listOf(
                NavigationViewModelTestParam(
                        whenAction = { it.navigateToUserAccount() },
                        expected = Destination.MPAccount
                ),
                NavigationViewModelTestParam(
                        whenAction = { it.toPrevious() },
                        expected = Destination.PreviousDestination
                ),
                NavigationViewModelTestParam(
                        whenAction = { it.performInnerNavigation(navDirectionsMock) },
                        expected = Destination.InnerDestination(navDirectionsMock)
                ),
                NavigationViewModelTestParam(
                        whenAction = { it.navigateToMovieCredits(10.0, "aTitle") },
                        expected = Destination.MPCredits(10.0, "aTitle")
                )
        )
    }
}