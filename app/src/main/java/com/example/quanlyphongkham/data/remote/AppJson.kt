package com.example.quanlyphongkham.data.remote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

/** Matches the backend's JsonNamingPolicy.SnakeCaseLower. */
@OptIn(ExperimentalSerializationApi::class)
val AppJson = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    ignoreUnknownKeys = true
    explicitNulls = false
    coerceInputValues = true
}
