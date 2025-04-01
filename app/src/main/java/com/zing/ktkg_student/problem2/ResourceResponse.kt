package com.zing.ktkg_student.problem2

data class ResourceResponse(
    val page: Int,
    val per_page: Int,
    val total: Int,
    val total_pages: Int,
    val data: List<ColorResource>,
    val support: Support
)

data class ColorResource(
    val id: Int,
    val name: String,
    val year: Int,
    val color: String,         //  "#98B2D1"
    val pantone_value: String
)

data class Support(
    val url: String,
    val text: String
)