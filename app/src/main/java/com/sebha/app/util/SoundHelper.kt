package com.sebha.app.util

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Extremely soft system tone for optional tap feedback.
 * Avoids shipping a binary asset — ToneGenerator is enough for a discreet click.
 */
class SoundHelper {

    private var toneGenerator: ToneGenerator? = null

    /** Plays a short, clearly audible click. Safe to call repeatedly. */
    fun playClick() {
        try {
            val generator = toneGenerator
                ?: ToneGenerator(AudioManager.STREAM_MUSIC, 90).also { toneGenerator = it }
            generator.startTone(ToneGenerator.TONE_PROP_ACK, 90)
        } catch (_: RuntimeException) {
            // ToneGenerator can fail on some devices; fail silently.
            release()
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
