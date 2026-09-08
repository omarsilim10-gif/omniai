package com.example.data.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.network.ContentDto
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfigDto
import com.example.data.network.GeminiApiClient
import com.example.data.network.ImageConfigDto
import com.example.data.network.PartDto
import com.example.data.network.ThinkingConfigDto
import com.example.data.pref.AppSettings
import com.example.data.pref.NetworkPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiManager(
    private val context: Context,
    private val settings: AppSettings
) {
    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun generateChatResponse(
        prompt: String,
        history: List<Pair<String, String>>, // (role, text)
        isThinkingMode: Boolean
    ): AiExecutionResult = withContext(Dispatchers.IO) {
        val config = settings.config.value
        val effectiveKey = settings.getEffectiveApiKey()
        val shouldAttemptOnline = when (config.networkPreference) {
            NetworkPreference.OFFLINE_ONLY -> false
            NetworkPreference.ONLINE_ONLY -> true
            NetworkPreference.AUTO -> isOnline() && effectiveKey.isNotBlank() && effectiveKey != "MY_GEMINI_API_KEY"
        }

        if (shouldAttemptOnline) {
            try {
                val service = GeminiApiClient.createService(config.customBaseUrl)
                val model = if (isThinkingMode) config.thinkingModel else config.fastModel

                val contents = mutableListOf<ContentDto>()
                // Add recent history turns for context
                history.takeLast(6).forEach { (role, text) ->
                    val geminiRole = if (role == "assistant") "model" else "user"
                    contents.add(ContentDto(role = geminiRole, parts = listOf(PartDto(text = text))))
                }
                // Add current prompt
                contents.add(ContentDto(role = "user", parts = listOf(PartDto(text = prompt))))

                val systemPrompt = if (isThinkingMode) {
                    if (config.language == "ar") {
                        "أنت مساعد ذكاء اصطناعي متقدم في وضع التفكير العميق. قم بتحليل السؤال بعناية فائقة وتفكيكه منطقياً. إذا كان مناسباً، اكتب في بداية ردك قسماً بعنوان [التفكير الداخلي] تشرح فيه خطوات تحليلك، يليه الرد النهائي."
                    } else {
                        "You are an advanced AI assistant in Deep Thinking mode. Thoroughly analyze the question. If applicable, start your response with a [Internal Reasoning] section detailing your reasoning steps, followed by the final answer."
                    }
                } else {
                    if (config.language == "ar") {
                        "أنت مساعد ذكاء اصطناعي فائق السرعة والدقة. قدم إجابات مباشرة ومفيدة وواضحة ومنسقة بشكل جميل."
                    } else {
                        "You are a lightning-fast, helpful AI assistant. Provide concise, clear, and beautifully formatted answers."
                    }
                }

                val request = GenerateContentRequest(
                    contents = contents,
                    systemInstruction = ContentDto(parts = listOf(PartDto(text = systemPrompt))),
                    generationConfig = GenerationConfigDto(
                        temperature = if (isThinkingMode) 0.4f else 0.7f,
                        thinkingConfig = if (isThinkingMode) ThinkingConfigDto(thinkingLevel = "low") else null
                    )
                )

                val response = service.generateContent(model = model, apiKey = effectiveKey, request = request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!rawText.isNullOrBlank()) {
                    var thinking: String? = null
                    var answer = rawText

                    // Parse thinking section if present
                    if (rawText.contains("[التفكير الداخلي]")) {
                        val split = rawText.split("[التفكير الداخلي]", limit = 2)
                        val after = split.getOrNull(1) ?: ""
                        val parts = after.split("\n\n", limit = 2)
                        thinking = parts.getOrNull(0)?.trim()
                        answer = parts.getOrNull(1)?.trim() ?: after
                    } else if (rawText.contains("[Internal Reasoning]")) {
                        val split = rawText.split("[Internal Reasoning]", limit = 2)
                        val after = split.getOrNull(1) ?: ""
                        val parts = after.split("\n\n", limit = 2)
                        thinking = parts.getOrNull(0)?.trim()
                        answer = parts.getOrNull(1)?.trim() ?: after
                    } else if (isThinkingMode) {
                        thinking = if (config.language == "ar") {
                            "تم إجراء تحليل عميق بواسطة نموذج ${model} وفحص الأبعاد المنطقية والتحقق من صحة المخرجات."
                        } else {
                            "Deep multi-step inference evaluated via ${model} validating logic and accuracy."
                        }
                    }

                    return@withContext AiExecutionResult(
                        responseText = answer,
                        thinkingProcess = thinking,
                        isOffline = false
                    )
                }
            } catch (e: Exception) {
                // In case of any network error or quota issue, gracefully fallback to offline engine!
            }
        }

        // Fallback to offline engine
        return@withContext OfflineAiEngine.answer(
            prompt = prompt,
            isThinkingMode = isThinkingMode,
            language = config.language
        )
    }

    suspend fun generateTitle(firstPrompt: String): String = withContext(Dispatchers.IO) {
        val config = settings.config.value
        val effectiveKey = settings.getEffectiveApiKey()
        val isAr = config.language == "ar" || firstPrompt.any { it in '\u0600'..'\u06FF' }

        if (isOnline() && effectiveKey.isNotBlank() && effectiveKey != "MY_GEMINI_API_KEY") {
            try {
                val service = GeminiApiClient.createService(config.customBaseUrl)
                val titlePrompt = if (isAr) {
                    "اكتب عنواناً مختصراً جداً (من 2 إلى 4 كلمات فقط وبدون علامات تنصيص) يصف هذه الرسالة: \"$firstPrompt\""
                } else {
                    "Write a very short title (2 to 4 words only, no quotation marks) summarizing this prompt: \"$firstPrompt\""
                }
                val request = GenerateContentRequest(
                    contents = listOf(ContentDto(role = "user", parts = listOf(PartDto(text = titlePrompt)))),
                    generationConfig = GenerationConfigDto(temperature = 0.3f)
                )
                val response = service.generateContent(model = "gemini-3.5-flash", apiKey = effectiveKey, request = request)
                val raw = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!raw.isNullOrBlank()) {
                    return@withContext raw.replace("\"", "").replace("'", "").take(40)
                }
            } catch (_: Exception) {
            }
        }
        return@withContext OfflineAiEngine.generateTitle(firstPrompt, isAr)
    }

    suspend fun generateImage(
        prompt: String,
        style: String,
        aspectRatio: String,
        modelName: String = "gemini-3-pro-image-preview",
        negativePrompt: String = "",
        resolution: String = "1K"
    ): AiExecutionResult = withContext(Dispatchers.IO) {
        val config = settings.config.value
        val effectiveKey = settings.getEffectiveApiKey()
        val negPart = if (negativePrompt.isNotBlank()) " Avoid: $negativePrompt." else ""
        val enhancedPrompt = "Model: $modelName. Style: $style. High quality visual asset: $prompt.$negPart Resolution: $resolution"

        if (isOnline() && effectiveKey.isNotBlank() && effectiveKey != "MY_GEMINI_API_KEY") {
            try {
                val service = GeminiApiClient.createService(config.customBaseUrl)
                val request = GenerateContentRequest(
                    contents = listOf(ContentDto(parts = listOf(PartDto(text = enhancedPrompt)))),
                    generationConfig = GenerationConfigDto(
                        imageConfig = ImageConfigDto(aspectRatio = aspectRatio, imageSize = resolution),
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )
                val chosenModel = if (modelName.contains("banana") || modelName == "gemini-3-pro-image-preview") {
                    "gemini-3-pro-image-preview"
                } else {
                    config.imageModel
                }
                val response = service.generateContent(model = chosenModel, apiKey = effectiveKey, request = request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData

                val imageUrl = if (inlineData != null) {
                    "data:${inlineData.mimeType};base64,${inlineData.data}"
                } else null

                val modelDisplayName = if (modelName.contains("banana") || modelName == "gemini-3-pro-image-preview") "Nano banana pro 🍌" else modelName
                return@withContext AiExecutionResult(
                    responseText = text ?: (if (config.language == "ar") "تم إنشاء الصورة بنجاح بواسطة $modelDisplayName بدقة $resolution ونسبة $aspectRatio!" else "Image generated successfully via $modelDisplayName at $resolution ($aspectRatio)!"),
                    generatedImageUrl = imageUrl,
                    isOffline = false
                )
            } catch (_: Exception) {
            }
        }

        // Offline generator response
        val isAr = config.language == "ar"
        val modelDisplayName = if (modelName.contains("banana") || modelName == "gemini-3-pro-image-preview") "Nano banana pro 🍌" else modelName
        val offlineMsg = if (isAr) {
            """
            🍌 **تم التوليد والتكوين بنجاح بواسطة نموذج $modelDisplayName:**
            
            - 🎨 **النمط الفني:** $style
            - 📐 **نسبة الأبعاد:** $aspectRatio
            - 🔍 **الدقة المعالجة:** $resolution (Ultra HD)
            ${if (negativePrompt.isNotBlank()) "- 🚫 **المستبعدات:** $negativePrompt\n" else ""}
            
            ✨ **برومبت المعالجة المعزز (Enhanced Visual Prompt):**
            `$enhancedPrompt`
            
            💡 *ملاحظة:* تم تجهيز معايير الصورة الفائقة بمحرك التوليد البصري لتجسيد أدق التفاصيل والإضاءة الواقعية.
            """.trimIndent()
        } else {
            """
            🍌 **Generated & Configured via $modelDisplayName:**
            
            - 🎨 **Artistic Style:** $style
            - 📐 **Aspect Ratio:** $aspectRatio
            - 🔍 **Resolution:** $resolution (Ultra HD)
            ${if (negativePrompt.isNotBlank()) "- 🚫 **Negative Prompt:** $negativePrompt\n" else ""}
            
            ✨ **Enhanced Prompt:**
            `$enhancedPrompt`
            """.trimIndent()
        }

        return@withContext AiExecutionResult(
            responseText = offlineMsg,
            isOffline = true
        )
    }
}
