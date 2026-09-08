package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apple
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AttachmentItem
import com.example.data.model.AttachmentType
import com.example.data.model.PackagePreset
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet

@Composable
fun PackagePickerDialog(
    onSelectPackage: (AttachmentItem) -> Unit,
    onPickFromStorage: () -> Unit,
    onDismiss: () -> Unit,
    isArabic: Boolean
) {
    val presets = listOf(
        PackagePreset(
            name = "OmniAI_Assistant_v2.5.apk",
            type = AttachmentType.APK,
            platform = "Android (APK)",
            sizeString = "38.4 MB",
            packageName = "com.aistudio.omniai",
            version = "2.5.0 (Build 250)",
            architecture = "arm64-v8a, x86_64",
            targetOs = "Android 15 (API 35) | minSdk 26",
            permissions = listOf("INTERNET", "RECORD_AUDIO", "POST_NOTIFICATIONS", "CAMERA"),
            signatureStatus = "V2/V3 Signed (SHA-256 Valid)",
            description = if (isArabic)
                "حزمة أندرويد متوافقة مع أحدث أنظمة أندرويد وتدعم المعالجات الحديثة 64-bit."
            else
                "Optimized Android Package built for 64-bit architectures with modern target SDK 35."
        ),
        PackagePreset(
            name = "OmniAI_Pro_v1.9.ipa",
            type = AttachmentType.IPA,
            platform = "Apple iOS (IPA)",
            sizeString = "62.1 MB",
            packageName = "com.aistudio.omniai.ios",
            version = "1.9.0 (Build 1900)",
            architecture = "arm64 Mach-O Universal",
            targetOs = "iOS 17.0+ (iPhone & iPad)",
            permissions = listOf("MicrophoneUsageDescription", "PhotoLibraryUsageDescription", "FaceID"),
            signatureStatus = "Apple Developer Team ID (Signed)",
            description = if (isArabic)
                "حزمة تطبيقات نظام Apple iOS تتضمن ملفات التوقيع و Mach-O ثنائي 64-bit."
            else
                "Apple iOS Application Archive with embedded provisioning profile and Mach-O binary."
        ),
        PackagePreset(
            name = "com.webos.omniai.tv_v1.2.ipk",
            type = AttachmentType.IPK,
            platform = "LG webOS / Smart TV (IPK)",
            sizeString = "22.8 MB",
            packageName = "com.webos.omniai.tv",
            version = "1.2.0",
            architecture = "all (Enact / WebKit)",
            targetOs = "webOS 6.0+ / Smart TV",
            permissions = listOf("media.tv", "com.webos.service.audio", "input.remote"),
            signatureStatus = "webOS Developer Key Verified",
            description = if (isArabic)
                "حزمة نظام تشغيل الشاشات الذكية webOS بتنسيق Debian-ar وتكامل Luna Bus API."
            else
                "Smart TV webOS Debian/ar package bundle with Luna Bus API declarations."
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF10B981), ElectricCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF031024), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "مدقق حزم وتطبيقات APK / IPA / IPK" else "APK / IPA / IPK Package Inspector",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "فحص الملفات والهندسة والأمان والصلاحيات" else "Inspect architecture, certificates, and manifest",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_package_picker_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action: Pick custom file from device storage
                Button(
                    onClick = onPickFromStorage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("pick_package_file_storage_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "📂 اختيار ملف من ذاكرة الجهاز (APK / IPA / IPK)" else "📂 Select File from Device Storage",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isArabic) "أو اختر حزمة جاهزة للتحليل الفوري بواسطة الذكاء الاصطناعي:" else "Or select a sample package for instant AI inspection:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                presets.forEach { preset ->
                    val colorAccent = when (preset.type) {
                        AttachmentType.APK -> Color(0xFF10B981) // Green
                        AttachmentType.IPA -> Color(0xFF38BDF8) // Sky Blue
                        AttachmentType.IPK -> Color(0xFFA855F7) // Purple
                        else -> ElectricCyan
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colorAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable {
                                val item = AttachmentItem(
                                    uri = null,
                                    name = preset.name,
                                    type = preset.type,
                                    sizeString = preset.sizeString,
                                    extraInfo = "${preset.packageName} • ${preset.version} • ${preset.architecture}"
                                )
                                onSelectPackage(item)
                            }
                            .testTag("package_preset_${preset.type.extensionTag}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colorAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (preset.type) {
                                                AttachmentType.APK -> Icons.Default.Android
                                                AttachmentType.IPA -> Icons.Default.Apple
                                                else -> Icons.Default.Tv
                                            },
                                            contentDescription = null,
                                            tint = colorAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = preset.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = preset.platform, fontSize = 11.sp, color = colorAccent, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colorAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = preset.sizeString, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorAccent)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "📦 ${preset.packageName} (v${preset.version})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "⚙️ ${preset.architecture} | 🎯 ${preset.targetOs}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
