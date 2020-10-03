package com.marchlab.haema.data.apis.model.kakao

data class KBookResponse (
    val meta: KBookMeta,
    val documents: List<KBookDocument>
)