package com.marchlab.haema.domain.model

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.android.parcel.Parcelize

enum class Emotion(val state: Int) {
    JOY(1),
    HAPPINESS(2),
    PROUD(3),
    TIRED(4),
    SADNESS(5),
    ANGER(6),
    FEAR(7),
    DEPRESSED(8),
    CALMNESS(9);

    companion object {
        fun valueOf(state: Int) = when(state) {
            1 -> JOY
            2 -> HAPPINESS
            3 -> PROUD
            4 -> TIRED
            5 -> SADNESS
            6 -> ANGER
            7 -> FEAR
            8 -> DEPRESSED
            9 -> CALMNESS
            else -> throw IllegalStateException()
        }
    }
}