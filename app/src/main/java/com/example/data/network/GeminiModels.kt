package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<ContentDto>,
    val generationConfig: GenerationConfigDto? = null,
    val systemInstruction: ContentDto? = null
)

@JsonClass(generateAdapter = true)
data class ContentDto(
    val role: String? = null,
    val parts: List<PartDto>
)

@JsonClass(generateAdapter = true)
data class PartDto(
    val text: String? = null,
    val inlineData: InlineDataDto? = null
)

@JsonClass(generateAdapter = true)
data class InlineDataDto(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfigDto(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: ThinkingConfigDto? = null,
    val imageConfig: ImageConfigDto? = null,
    val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ThinkingConfigDto(
    val thinkingLevel: String = "low"
)

@JsonClass(generateAdapter = true)
data class ImageConfigDto(
    val aspectRatio: String = "1:1",
    val imageSize: String = "1K"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<CandidateDto>? = null,
    val error: GeminiErrorDto? = null
)

@JsonClass(generateAdapter = true)
data class CandidateDto(
    val content: ContentDto? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiErrorDto(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
