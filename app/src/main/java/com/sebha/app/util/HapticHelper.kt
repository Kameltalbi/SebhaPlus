package com.sebha.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Lightweight haptic feedback for counter taps and milestones.
 * All vibrations are short to stay premium and unobtrusive.
 */
class HapticHelper(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /** Clear tick used for a normal +1 tap. */
    fun tick() = vibrate(35)

    /** Distinct pattern when the count hits a traditional milestone. */
    fun milestone() = vibratePattern(longArrayOf(0, 45, 50, 45))

    /** Stronger confirmation when the configured goal is reached. */
    fun goalReached() = vibratePattern(longArrayOf(0, 60, 60, 60, 60, 80))

    private fun vibrate(durationMs: Long) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(durationMs)
        }
    }

    private fun vibratePattern(timings: LongArray) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(timings, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(timings, -1)
        }
    }
}
