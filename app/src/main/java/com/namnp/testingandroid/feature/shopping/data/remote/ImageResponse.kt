package com.namnp.testingandroid.feature.shopping.data.remote

data class ImageResponse(
    val hits: List<ImageResult>,
    val total: Int,
    val totalHits: Int
)