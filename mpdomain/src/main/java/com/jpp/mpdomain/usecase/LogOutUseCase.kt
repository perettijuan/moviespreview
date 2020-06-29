package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to perform the logout of the user.
 */
class LogOutUseCase(
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val moviePageRepository: MoviePageRepository
) {

    suspend fun execute(): Try<Unit> {
        accountRepository.flushUserAccountData()
        moviePageRepository.flushFavoriteMoviePages()
        moviePageRepository.flushRatedMoviePages()
        moviePageRepository.flushWatchlistMoviePages()
        sessionRepository.deleteCurrentSession()
        return Try.Success(Unit)
    }
}
