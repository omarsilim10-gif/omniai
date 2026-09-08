package com.example.data.model

import android.graphics.Bitmap

enum class AttachmentType(val labelAr: String, val labelEn: String, val extensionTag: String) {
    IMAGE("صورة", "Image", "IMG"),
    APK("تطبيق أندرويد", "Android Package", "APK"),
    IPA("تطبيق آبل iOS", "Apple iOS Package", "IPA"),
    IPK("تطبيق webOS شاشة ذكية", "webOS Package", "IPK"),
    DOCUMENT("مستند / ملف", "Document", "DOC"),
    VIDEO("فيديو ذكاء اصطناعي", "AI Video", "MP4")
}

data class AttachmentItem(
    val uri: String? = null,
    val name: String,
    val type: AttachmentType,
    val sizeString: String,
    val extraInfo: String? = null,
    val thumbnailBitmap: Bitmap? = null
)

data class PackagePreset(
    val name: String,
    val type: AttachmentType,
    val platform: String,
    val sizeString: String,
    val packageName: String,
    val version: String,
    val architecture: String,
    val targetOs: String,
    val permissions: List<String>,
    val signatureStatus: String,
    val description: String
)
