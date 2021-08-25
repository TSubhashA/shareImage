package com.subhasha.myapplication.data

data class Data(
    val brand_id: String,
    val brand_name: String,
    val brand_slug: String,
    val category_name: String,
    val product_cart: List<Any>,
    val product_code: String,
    val product_description: String,
    val product_favourite: Boolean,
    val product_featured_status: String,
    val product_gallery: List<ProductGallery>,
    val product_id: String,
    val product_image: String,
    val product_min_order_qty: String,
    val product_packing_type: String,
    val product_similar_tags: String,
    val product_slug: String,
    val product_stock_type: String,
    val product_tags: String,
    val product_title: String,
    val product_total_stock: String,
    val product_variants: String,
    val selected_variant: SelectedVariant,
    val selected_variant_group_names: List<SelectedVariantGroupName>,
    val variant_group_names: List<VariantGroupName>
)