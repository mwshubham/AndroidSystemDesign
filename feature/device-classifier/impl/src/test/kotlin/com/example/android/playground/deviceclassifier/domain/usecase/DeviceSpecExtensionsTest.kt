package com.example.android.playground.deviceclassifier.domain.usecase

import com.example.android.playground.deviceclassifier.domain.model.DeviceSpec
import com.example.android.playground.deviceclassifier.domain.model.DeviceTier
import com.example.android.playground.deviceclassifier.domain.model.computeTier
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceSpecExtensionsTest {

    private fun createSpec(ramMb: Long, cpuCores: Int) = DeviceSpec(
        ramMb = ramMb,
        cpuCores = cpuCores,
        apiLevel = 33
    )

    // --- Happy path ---

    @Test
    fun `low ram and low cpu cores returns LOW tier`() {
        val spec = createSpec(ramMb = 1_024L, cpuCores = 2)
        assertEquals(DeviceTier.LOW, spec.computeTier())
    }

    @Test
    fun `medium ram and medium cpu cores returns MEDIUM tier`() {
        val spec = createSpec(ramMb = 3_000L, cpuCores = 5)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    @Test
    fun `high ram and high cpu cores returns HIGH tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 8)
        assertEquals(DeviceTier.HIGH, spec.computeTier())
    }

    // --- RAM boundary tests ---

    @Test
    fun `ram just below 2048 MB maps to LOW tier`() {
        val spec = createSpec(ramMb = 2_047L, cpuCores = 8)
        assertEquals(DeviceTier.LOW, spec.computeTier())
    }

    @Test
    fun `ram at exactly 2048 MB maps to MEDIUM tier`() {
        val spec = createSpec(ramMb = 2_048L, cpuCores = 5)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    @Test
    fun `ram at exactly 4096 MB maps to MEDIUM tier`() {
        val spec = createSpec(ramMb = 4_096L, cpuCores = 5)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    @Test
    fun `ram just above 4096 MB maps to HIGH tier`() {
        val spec = createSpec(ramMb = 4_097L, cpuCores = 8)
        assertEquals(DeviceTier.HIGH, spec.computeTier())
    }

    // --- CPU boundary tests ---

    @Test
    fun `cpu below 4 cores maps to LOW tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 3)
        assertEquals(DeviceTier.LOW, spec.computeTier())
    }

    @Test
    fun `cpu at exactly 4 cores maps to MEDIUM tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 4)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    @Test
    fun `cpu at exactly 6 cores maps to MEDIUM tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 6)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    @Test
    fun `cpu above 6 cores maps to HIGH tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 7)
        assertEquals(DeviceTier.HIGH, spec.computeTier())
    }

    // --- Conservative min-of logic ---

    @Test
    fun `high ram with low cpu cores returns LOW tier`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 2)
        assertEquals(DeviceTier.LOW, spec.computeTier())
    }

    @Test
    fun `low ram with high cpu cores returns LOW tier`() {
        val spec = createSpec(ramMb = 1_024L, cpuCores = 8)
        assertEquals(DeviceTier.LOW, spec.computeTier())
    }

    @Test
    fun `medium ram with high cpu cores returns MEDIUM tier`() {
        val spec = createSpec(ramMb = 3_000L, cpuCores = 8)
        assertEquals(DeviceTier.MEDIUM, spec.computeTier())
    }

    // --- Simulated values tests ---

    @Test
    fun `computeTier with custom parameter overrides default receiver values`() {
        val spec = createSpec(ramMb = 6_144L, cpuCores = 8) // Defaults to HIGH
        
        // Override with LOW values
        assertEquals(DeviceTier.LOW, spec.computeTier(ramMb = 1_024L, cpuCores = 2))
        
        // Override RAM only to MEDIUM
        assertEquals(DeviceTier.MEDIUM, spec.computeTier(ramMb = 3_000L))
        
        // Override CPU only to MEDIUM
        assertEquals(DeviceTier.MEDIUM, spec.computeTier(cpuCores = 5))
    }
}
