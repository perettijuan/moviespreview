package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.interactor.GetMovieDetails
import com.jpp.moviespreview.domainlayer.interactor.MovieDetailsParam
import com.jpp.moviespreview.domainlayer.interactor.MovieDetailsResult
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class GetMovieDetailsTest {

    @MockK
    private lateinit var moviesRepository: MoviesRepository
    @MockK
    private lateinit var connectivityVerifier: ConnectivityVerifier

    private lateinit var subject: GetMovieDetails

    @BeforeEach
    fun setUp() {
        subject = GetMovieDetailsImpl(moviesRepository, connectivityVerifier)
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should show correct error when repo fails`(connectedToNetwork: Boolean,
                                                    expectedResult: MovieDetailsResult) {
        val movieId = 12.toDouble()

        every { moviesRepository.getMovieDetail(movieId) } returns MoviesRepository.MoviesRepositoryOutput.Error
        every { connectivityVerifier.isConnectedToNetwork() } returns connectedToNetwork

        val actual = subject.execute(MovieDetailsParam(movieId))

        assertEquals(expectedResult, actual)
    }


    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                Arguments.arguments(true, MovieDetailsResult.ErrorUnknown),
                Arguments.arguments(false, MovieDetailsResult.ErrorNoConnectivity)
        )
    }

}