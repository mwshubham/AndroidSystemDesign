package com.example.android.playground.deviceclassifier.domain.model

data class DeviceSpec(
    val ramMb: Long,
    val cpuCores: Int,
    val apiLevel: Int,
)

fun DeviceSpec.computeTier(
    ramMb: Long = this.ramMb,
    cpuCores: Int = this.cpuCores,
): DeviceTier {
    val ramTier = when {
        ramMb < RAM_LOW_THRESHOLD_MB -> DeviceTier.LOW
        ramMb <= RAM_MEDIUM_THRESHOLD_MB -> DeviceTier.MEDIUM
        else -> DeviceTier.HIGH
    }
    val cpuTier = when {
        cpuCores < CPU_LOW_THRESHOLD -> DeviceTier.LOW
        cpuCores <= CPU_MEDIUM_THRESHOLD -> DeviceTier.MEDIUM
        else -> DeviceTier.HIGH
    }
    return minOf(ramTier, cpuTier)
}

private const val RAM_LOW_THRESHOLD_MB = 2_048L
private const val RAM_MEDIUM_THRESHOLD_MB = 4_096L
private const val CPU_LOW_THRESHOLD = 4
private const val CPU_MEDIUM_THRESHOLD = 6

