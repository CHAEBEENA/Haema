package com.marchlab.haema.util.extensions

import com.marchlab.haema.R
import com.marchlab.haema.domain.model.Emotion

val Emotion.rawResId get() = when(ordinal) {
    0 -> R.raw.joy
    1 -> R.raw.happiness
    2 -> R.raw.proud
    3 -> R.raw.tired
    4 -> R.raw.sadness
    5 -> R.raw.anger
    6 -> R.raw.fear
    7 -> R.raw.depressed
    8 -> R.raw.calmness
    else -> R.raw.calmness
}

val Emotion.drawableResId get() = when(ordinal) {
    0 -> R.drawable.img_list_emo_joy
    1 -> R.drawable.img_list_emo_happiness
    2 -> R.drawable.img_list_emo_proud
    3 -> R.drawable.img_list_emo_tired
    4 -> R.drawable.img_list_emo_sadness
    5 -> R.drawable.img_list_emo_anger
    6 -> R.drawable.img_list_emo_fear
    7 -> R.drawable.img_list_emo_depressed
    8 -> R.drawable.img_list_emo_calmness
    else -> R.raw.calmness
}

val Emotion.stateName get() = when(ordinal) {
    0 -> "기쁨"
    1 -> "행복"
    2 -> "뿌듯"
    3 -> "피곤"
    4 -> "슬픔"
    5 -> "화남"
    6 -> "불안"
    7 -> "우울"
    8 -> "평온"
    else -> "평온"
}