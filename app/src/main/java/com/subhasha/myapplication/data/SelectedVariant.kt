package com.subhasha.myapplication.data

data class SelectedVariant(
    val variant_id: String,
    val variant_images: List<Any>,
    val variant_including_tax: String,
    val variant_inner_case: String,
    val variant_master_packing: String,
    val variant_mrp_price: String,
    val variant_regular_price: String,
    val variant_size: String,
    val variant_weight: String
)