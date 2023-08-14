package br.com.duannistontriocodechallenge.core


data class BusinessError(
    val success: Boolean,
    val message: String,
    val error: Error?
)


data class Error(
    val title: String?,
    val message: String?,
    val tag: String?
)
