package com.example.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class SoundEffectsManager(private val scope: CoroutineScope) {

    var isSoundEnabled: Boolean = true

    fun playClick() {
        if (!isSoundEnabled) return
        playToneSequence(listOf(ToneNote(580.0, 45, 0.25f)))
    }

    fun playTileOpen() {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(
                ToneNote(523.25, 65, 0.32f), // C5
                ToneNote(659.25, 75, 0.35f), // E5
                ToneNote(783.99, 110, 0.38f) // G5
            )
        )
    }

    fun playRevealAnswer() {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(
                ToneNote(587.33, 70, 0.32f), // D5
                ToneNote(880.00, 140, 0.40f) // A5
            )
        )
    }

    fun playRouletteTick(pitchMultiplier: Double = 1.0) {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(ToneNote(440.0 * pitchMultiplier, 35, 0.28f))
        )
    }

    fun playCountdownTick(urgent: Boolean = false) {
        if (!isSoundEnabled) return
        val freq = if (urgent) 880.0 else 620.0
        playToneSequence(listOf(ToneNote(freq, 40, 0.25f)))
    }

    fun playAwardFanfare() {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(
                ToneNote(523.25, 90, 0.38f),  // C5
                ToneNote(659.25, 90, 0.38f),  // E5
                ToneNote(783.99, 90, 0.40f),  // G5
                ToneNote(1046.50, 220, 0.45f) // C6
            )
        )
    }

    fun playDrumrollEffect() {
        if (!isSoundEnabled) return
        val notes = (0 until 10).map { i ->
            ToneNote(180.0 + (i * 22.0), 45, 0.30f)
        } + ToneNote(783.99, 180, 0.42f)
        playToneSequence(notes)
    }

    fun playCheerChord() {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(
                ToneNote(440.0, 70, 0.35f),
                ToneNote(554.37, 70, 0.35f),
                ToneNote(659.25, 70, 0.38f),
                ToneNote(880.0, 200, 0.45f)
            )
        )
    }

    fun playBreathingChime() {
        if (!isSoundEnabled) return
        playToneSequence(
            listOf(
                ToneNote(432.0, 220, 0.25f),
                ToneNote(528.0, 320, 0.22f)
            )
        )
    }

    private data class ToneNote(
        val freqHz: Double,
        val durationMs: Int,
        val volume: Float
    )

    private fun playToneSequence(notes: List<ToneNote>) {
        scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                val totalSamples = notes.sumOf { (sampleRate * it.durationMs) / 1000 }
                if (totalSamples <= 0) return@launch

                val pcmBuffer = ShortArray(totalSamples)
                var offset = 0

                for (note in notes) {
                    val count = (sampleRate * note.durationMs) / 1000
                    for (i in 0 until count) {
                        val env = when {
                            i < count * 0.1 -> i / (count * 0.1)
                            i > count * 0.7 -> (count - i) / (count * 0.3)
                            else -> 1.0
                        }.coerceIn(0.0, 1.0)
                        val angle = 2.0 * PI * i * note.freqHz / sampleRate
                        val raw = (sin(angle) + 0.25 * sin(angle * 2.0)) * 0.8
                        val sample = (raw * env * note.volume * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        if (offset + i < pcmBuffer.size) {
                            pcmBuffer[offset + i] = sample.toShort()
                        }
                    }
                    offset += count
                }

                val byteCount = pcmBuffer.size * 2
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(byteCount, minBuf)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmBuffer, 0, pcmBuffer.size)
                track.play()
                val totalDurationMs = notes.sumOf { it.durationMs }.toLong()
                delay(totalDurationMs + 80L)
                track.stop()
                track.release()
            } catch (_: Throwable) {
                // Ignore audio hardware unavailability on headless JVM / emulator
            }
        }
    }
}

class MicrophoneEnergyMeter(private val scope: CoroutineScope) {
    private var recordingJob: Job? = null

    val isRecording: Boolean
        get() = recordingJob?.isActive == true

    fun hasMicPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startListening(
        context: Context,
        onLevelNormalized: (Float) -> Unit,
        onError: () -> Unit
    ) {
        if (!hasMicPermission(context)) {
            onError()
            return
        }
        stopListening()
        recordingJob = scope.launch(Dispatchers.IO) {
            var audioRecord: AudioRecord? = null
            try {
                val sampleRate = 16000
                val minBuf = AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (minBuf <= 0) {
                    onError()
                    return@launch
                }
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuf * 2
                )
                if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                    onError()
                    return@launch
                }
                audioRecord.startRecording()
                val buffer = ShortArray(minBuf)
                while (isActive) {
                    val readCount = audioRecord.read(buffer, 0, buffer.size)
                    if (readCount > 0) {
                        var sumSquares = 0.0
                        var peak = 0
                        for (i in 0 until readCount) {
                            val s = abs(buffer[i].toInt())
                            if (s > peak) peak = s
                            sumSquares += (s * s).toDouble()
                        }
                        val rms = sqrt(sumSquares / readCount)
                        // Normalize RMS & peak into 0f..100f applause excitement level
                        val normalized = min(100f, ((rms / 4500.0) * 85.0 + (peak / 18000.0) * 35.0).toFloat())
                        onLevelNormalized(normalized)
                    }
                    delay(65L)
                }
            } catch (_: Throwable) {
                onError()
            } finally {
                try {
                    audioRecord?.stop()
                    audioRecord?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun stopListening() {
        recordingJob?.cancel()
        recordingJob = null
    }
}
