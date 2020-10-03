package com.marchlab.haema.data.apis.model.kakao

import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat

class KBookDocument (
    val authors: List<String>,
    val datetime: String,
    val publisher: String,
    val thumbnail: String,
    val title: String
)