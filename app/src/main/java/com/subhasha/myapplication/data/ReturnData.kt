package com.subhasha.myapplication.data

data class ReturnData(
    val `data`: Data,
    val image_base_path: String,
    val message: String,
    val success: Boolean
)