package com.badimala.lightsweeper2

import android.content.Context
import android.media.SoundPool
import android.media.AudioAttributes

class SoundEffects private constructor(context: Context){

    private var soundPool: SoundPool? = null
    private var soundIndex = 0
    private var incorrectGameSoundId = 0
    private var correctGameSoundId = 0

    companion object {
        private var instance: SoundEffects? = null

        fun getInstance(context: Context): SoundEffects {
            if (instance == null) {
                instance = SoundEffects(context)
            }
            return instance!!
        }
    }

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        soundPool?.let {
            incorrectGameSoundId = it.load(context, R.raw.ding_ui_button_click_5, 1)
            correctGameSoundId = it.load(context, R.raw.correct_ui_button_click_3, 1)
        }

        resetTones()
    }

    fun resetTones() {
        soundIndex = -1
    }

    fun playCorrect() {
        soundPool?.play(correctGameSoundId, 1f, 1f, 1, 1, 1f)
    }

    fun playIncorrect() {
        soundPool?.play(incorrectGameSoundId, 0.75f, 0.75f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}