package com.kingsaul22.etxcenter.feature.auth

import app.cash.turbine.test
import com.kingsaul22.etxcenter.domain.repository.AuthResult
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @Test
    fun `authentication emits Loading then Authenticated on success`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val mockRepo: IAuthRepository = mockk()
            coEvery { mockRepo.signInSilently() } returns AuthResult.Success

            val viewModel = AuthViewModel(mockRepo)

            viewModel.uiState.test {
                assertEquals(AuthUiState.Loading, awaitItem())

                testScheduler.advanceUntilIdle()

                assertEquals(AuthUiState.Authenticated, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `authentication emits Loading then Error on failure`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val error = RuntimeException("Network unavailable")
            val mockRepo: IAuthRepository = mockk()
            coEvery { mockRepo.signInSilently() } returns AuthResult.Failure(error)

            val viewModel = AuthViewModel(mockRepo)

            viewModel.uiState.test {
                assertEquals(AuthUiState.Loading, awaitItem())

                testScheduler.advanceUntilIdle()

                assertEquals(AuthUiState.Error("Network unavailable"), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `retry after error emits Loading then Authenticated`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val error = RuntimeException("First attempt failed")
            val mockRepo: IAuthRepository = mockk()
            coEvery { mockRepo.signInSilently() } returns AuthResult.Failure(error) andThen AuthResult.Success

            val viewModel = AuthViewModel(mockRepo)

            viewModel.uiState.test {
                assertEquals(AuthUiState.Loading, awaitItem())
                testScheduler.advanceUntilIdle()
                assertEquals(AuthUiState.Error("First attempt failed"), awaitItem())

                viewModel.authenticate()
                assertEquals(AuthUiState.Loading, awaitItem())

                testScheduler.advanceUntilIdle()
                assertEquals(AuthUiState.Authenticated, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }
}
