package com.marchlab.haema.data.apis.model.kakao

import com.google.gson.annotations.SerializedName

data class KBookMeta (
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)