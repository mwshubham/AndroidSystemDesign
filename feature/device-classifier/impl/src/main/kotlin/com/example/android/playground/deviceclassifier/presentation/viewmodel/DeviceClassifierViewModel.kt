package com.example.android.playground.deviceclassifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playground.deviceclassifier.domain.model.DeviceTier
import com.example.android.playground.deviceclassifier.domain.model.computeTier
import com.example.android.playground.deviceclassifier.domain.usecase.GetDeviceSpecUseCase
import com.example.android.playground.deviceclassifier.presentation.intent.DeviceClassifierIntent
import com.example.android.playground.deviceclassifier.presentation.sideeffect.DeviceClassifierSideEffect
import com.example.android.playground.deviceclassifier.presentation.state.DeviceClassifierState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DeviceClassifierViewModel
    @Inject
    constructor(
        private val getDeviceSpec: GetDeviceSpecUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(DeviceClassifierState())
        val state: StateFlow<DeviceClassifierState> = _state.asStateFlow()

        private val _sideEffect = Channel<DeviceClassifierSideEffect>(Channel.BUFFERED)
        val sideEffect = _sideEffect.receiveAsFlow()

        init {
            handleIntent(DeviceClassifierIntent.LoadDeviceSpec)
        }

        fun handleIntent(intent: DeviceClassifierIntent) {
            Timber.d("handleIntent: $intent")
            when (intent) {
                is DeviceClassifierIntent.LoadDeviceSpec -> loadDeviceSpec()
                is DeviceClassifierIntent.UpdateSimulatedRam -> updateSimulatedRam(intent.ramMb)
                is DeviceClassifierIntent.UpdateSimulatedCpuCores -> updateSimulatedCpuCores(intent.cores)
                is DeviceClassifierIntent.ResetToDeviceDefaults -> resetToDeviceDefaults()
                is DeviceClassifierIntent.NavigateBack -> sendEffect(DeviceClassifierSideEffect.NavigateBack)
            }
        }

        private fun loadDeviceSpec() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                runCatching { getDeviceSpec() }
                    .onSuccess { spec ->
                        Timber.d("Device spec loaded: $spec")
                        val tier = spec.computeTier()
                        _state.update {
                            it.copy(
                                actualSpec = spec,
                                simulatedRamMb = spec.ramMb,
                                simulatedCpuCores = spec.cpuCores,
                                effectiveTier = tier,
                                isLoading = false,
                            )
                        }
                    }.onFailure { error ->
                        Timber.e(error, "Failed to load device spec")
                        _state.update { it.copy(isLoading = false, error = error.message) }
                        sendEffect(DeviceClassifierSideEffect.ShowError(error.message ?: "Unknown error"))
                    }
            }
        }

        private fun updateSimulatedRam(ramMb: Long) {
            val tier = _state.value.actualSpec?.computeTier(
                ramMb = ramMb,
                cpuCores = _state.value.simulatedCpuCores
            ) ?: DeviceTier.LOW
            _state.update { it.copy(simulatedRamMb = ramMb, effectiveTier = tier) }
        }

        private fun updateSimulatedCpuCores(cores: Int) {
            val tier = _state.value.actualSpec?.computeTier(
                ramMb = _state.value.simulatedRamMb,
                cpuCores = cores
            ) ?: DeviceTier.LOW
            _state.update { it.copy(simulatedCpuCores = cores, effectiveTier = tier) }
        }

        private fun resetToDeviceDefaults() {
            val spec = _state.value.actualSpec ?: return
            val tier = spec.computeTier()
            _state.update {
                it.copy(
                    simulatedRamMb = spec.ramMb,
                    simulatedCpuCores = spec.cpuCores,
                    effectiveTier = tier,
                )
            }
        }

        private fun sendEffect(effect: DeviceClassifierSideEffect) {
            viewModelScope.launch { _sideEffect.send(effect) }
        }
    }
