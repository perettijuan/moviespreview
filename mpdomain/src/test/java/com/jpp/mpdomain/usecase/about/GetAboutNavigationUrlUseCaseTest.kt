package com.jpp.mpdomain.usecase.about

import com.jpp.mpdomain.repository.AboutUrlRepository
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class GetAboutNavigationUrlUseCaseTest {

    @RelaxedMockK
    private lateinit var repository: AboutUrlRepository

    private lateinit var subject: GetAboutNavigationUrlUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAboutNavigationUrlUseCase.Impl(repository)
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun testGetUrlFor(param: AboutNavigationUseCaseParams) {
        subject.getUrlFor(param.navigationType)
        param.verification.invoke(repository)
    }

    data class AboutNavigationUseCaseParams(
            val navigationType: AboutNavigationType,
            val verification: (AboutUrlRepository) -> Unit
    )

    companion object {
        @JvmStatic
        fun executeParameters() = listOf(
                AboutNavigationUseCaseParams(
                        navigationType = AboutNavigationType.TheMovieDbTermsOfUse,
                        verification = { repo -> verify { repo.getTheMovieDbTermOfUseUrl() } }),
                AboutNavigationUseCaseParams(
                        navigationType = AboutNavigationType.AppCodeRepo,
                        verification = { repo -> verify { repo.getCodeRepoUrl() } }),
                AboutNavigationUseCaseParams(
                        navigationType = AboutNavigationType.GooglePlayApp,
                        verification = { repo -> verify { repo.getGPlayAppUrl() } }),
                AboutNavigationUseCaseParams(
                        navigationType = AboutNavigationType.GooglePlayWeb,
                        verification = { repo -> verify { repo.getGPlayWebUrl() } }),
                AboutNavigationUseCaseParams(
                        navigationType = AboutNavigationType.ShareApp,
                        verification = { repo -> verify { repo.getSharingUrl() } })
        )
    }
}