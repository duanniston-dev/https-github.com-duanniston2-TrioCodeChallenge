package br.com.duannistontriocodechallenge.core

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDialog(val title: String, val message: String)