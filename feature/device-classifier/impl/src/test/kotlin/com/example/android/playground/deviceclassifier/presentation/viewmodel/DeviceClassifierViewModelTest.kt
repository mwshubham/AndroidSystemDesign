package com.example.android.playground.deviceclassifier.presentation.viewmodel

import app.cash.turbine.test
import com.example.android.playground.core.testing.MainDispatcherRule
import com.example.android.playground.deviceclassifier.domain.model.DeviceSpec
import com.example.android.playground.deviceclassifier.domain.model.DeviceTier
import com.example.android.playground.deviceclassifier.domain.usecase.GetDeviceSpecUseCase
import com.example.android.playground.deviceclassifier.presentation.intent.DeviceClassifierIntent
import com.example.android.playground.deviceclassifier.presentation.sideeffect.DeviceClassifierSideEffect
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DeviceClassifierViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDeviceSpec: GetDeviceSpecUseCase = mockk()

    private val testSpec = DeviceSpec(ramMb = 6_144L, cpuCores = 8, apiLevel = 33)

    private fun createViewModel() = DeviceClassifierViewModel(getDeviceSpec)

    @Test
    fun `init loads device spec and updates state`() =
        runTest {
            coEvery { getDeviceSpec() } returns testSpec

            val viewModel = createViewModel()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(testSpec, state.actualSpec)
                assertEquals(testSpec.ramMb, state.simulatedRamMb)
                assertEquals(testSpec.cpuCores, state.simulatedCpuCores)
                assertEquals(DeviceTier.HIGH, state.effectiveTier)
                assertFalse(state.isLoading)
                assertNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `LoadDeviceSpec failure sets error state`() =
        runTest {
            val errorMessage = "Network unavailable"
            coEvery { getDeviceSpec() } throws RuntimeException(errorMessage)

            val viewModel = createViewModel()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(errorMessage, state.error)
                assertFalse(state.isLoading)
                assertNull(state.actualSpec)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `LoadDeviceSpec failure emits ShowError side effect`() =
        runTest {
            val errorMessage = "Network unavailable"
            coEvery { getDeviceSpec() } throws RuntimeException(errorMessage)

            val viewModel = createViewModel()

            viewModel.sideEffect.test {
                assertEquals(DeviceClassifierSideEffect.ShowError(errorMessage), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateSimulatedRam updates ram and recomputes effective tier`() =
        runTest {
            coEvery { getDeviceSpec() } returns testSpec
            val viewModel = createViewModel()

            viewModel.handleIntent(DeviceClassifierIntent.UpdateSimulatedRam(1_024L))

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(1_024L, state.simulatedRamMb)
                assertEquals(DeviceTier.LOW, state.effectiveTier)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateSimulatedCpuCores updates cores and recomputes effective tier`() =
        runTest {
            coEvery { getDeviceSpec() } returns testSpec
            val viewModel = createViewModel()

            viewModel.handleIntent(DeviceClassifierIntent.UpdateSimulatedCpuCores(2))

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(2, state.simulatedCpuCores)
                assertEquals(DeviceTier.LOW, state.effectiveTier)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ResetToDeviceDefaults reverts simulated values to actual spec`() =
        runTest {
            coEvery { getDeviceSpec() } returns testSpec
            val viewModel = createViewModel()

            viewModel.handleIntent(DeviceClassifierIntent.UpdateSimulatedRam(1_024L))
            viewModel.handleIntent(DeviceClassifierIntent.ResetToDeviceDefaults)

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(testSpec.ramMb, state.simulatedRamMb)
                assertEquals(testSpec.cpuCores, state.simulatedCpuCores)
                assertEquals(DeviceTier.HIGH, state.effectiveTier)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ResetToDeviceDefaults is noop when actual spec is null`() =
        runTest {
            coEvery { getDeviceSpec() } throws RuntimeException("error")
            val viewModel = createViewModel()

            val stateBefore = viewModel.state.value
            viewModel.handleIntent(DeviceClassifierIntent.ResetToDeviceDefaults)
            val stateAfter = viewModel.state.value

            assertEquals(stateBefore.simulatedRamMb, stateAfter.simulatedRamMb)
            assertEquals(stateBefore.simulatedCpuCores, stateAfter.simulatedCpuCores)
            assertNull(stateAfter.actualSpec)
        }

    @Test
    fun `NavigateBack intent emits NavigateBack side effect`() =
        runTest {
            coEvery { getDeviceSpec() } returns testSpec
            val viewModel = createViewModel()

            viewModel.sideEffect.test {
                viewModel.handleIntent(DeviceClassifierIntent.NavigateBack)
                assertEquals(DeviceClassifierSideEffect.NavigateBack, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
