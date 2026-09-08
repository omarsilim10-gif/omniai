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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MovieCreation
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmberThinking
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet

@Composable
fun AttachmentActionSheet(
    onPickImage: () -> Unit,
    onPickPackage: () -> Unit,
    onPickDocument: () -> Unit,
    onOpenNanoBananaPro: () -> Unit,
    onOpenProVideoStudio: () -> Unit,
    onDismiss: () -> Unit,
    isArabic: Boolean
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                                .background(Brush.linearGradient(listOf(ElectricCyan, NeonViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "إرفاق وإنشاء بالذكاء الاصطناعي" else "Attach & Generate with AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "صور، ملفات، حزم APK/IPA/IPK، واستوديو توليدي" else "Photos, files, app packages, and AI studios",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_attachment_sheet_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions Grid / List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Option 1: Image
                    AttachmentOptionRow(
                        title = if (isArabic) "📷 إرسال صورة أو لقطة شاشة" else "📷 Send Photo or Screenshot",
                        subtitle = if (isArabic) "تحليل الرؤية البصرية بالذكاء الاصطناعي" else "Multimodal visual reasoning & OCR",
                        accentColor = ElectricCyan,
                        icon = Icons.Default.AddPhotoAlternate,
                        testTag = "action_pick_photo",
                        onClick = onPickImage
                    )

                    // Option 2: Packages APK / IPA / IPK
                    AttachmentOptionRow(
                        title = if (isArabic) "📦 فحص وإرسال حزم (APK / IPA / IPK)" else "📦 Packages & Apps (APK / IPA / IPK)",
                        subtitle = if (isArabic) "تحليل بنيوي وهندسي وأمني وصلاحيات الحزم" else "Inspect binary, certificates, and manifest",
                        accentColor = Color(0xFF10B981),
                        icon = Icons.Default.Inventory2,
                        testTag = "action_pick_package",
                        badge = "APK • IPA • IPK",
                        onClick = onPickPackage
                    )

                    // Option 3: Document / File
                    AttachmentOptionRow(
                        title = if (isArabic) "📁 إرسال ملف أو مستند (PDF / Code / TXT)" else "📁 Send File or Document",
                        subtitle = if (isArabic) "فحص الأكواد والملفات وتلخيصها" else "Analyze code, documents, or data",
                        accentColor = Color(0xFF38BDF8),
                        icon = Icons.Default.Description,
                        testTag = "action_pick_doc",
                        onClick = onPickDocument
                    )

                    // Option 4: Nano Banana Pro Image Generator
                    AttachmentOptionRow(
                        title = if (isArabic) "🍌 توليد صور بواسطة Nano banana pro" else "🍌 Generate with Nano Banana Pro",
                        subtitle = if (isArabic) "نموذج فائق السرعة والدقة الفائقة 4K" else "Ultra-fast hyper-detailed 4K generation",
                        accentColor = AmberThinking,
                        icon = Icons.Default.AutoAwesome,
                        testTag = "action_nano_banana_pro",
                        badge = "Nano Banana Pro ✨",
                        onClick = onOpenNanoBananaPro
                    )

                    // Option 5: Pro AI Video Studio
                    AttachmentOptionRow(
                        title = if (isArabic) "🎬 صناعة فيديو احترافي بالذكاء الاصطناعي" else "🎬 Pro AI Video Creator & Director",
                        subtitle = if (isArabic) "إخراج المشاهد وحركات الكاميرا والسيناريو" else "Cinematic camera motions, fps & storyboard",
                        accentColor = NeonViolet,
                        icon = Icons.Default.MovieCreation,
                        testTag = "action_pro_video_studio",
                        badge = "Veo 3.1 🎥",
                        onClick = onOpenProVideoStudio
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentOptionRow(
    title: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    testTag: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    badge?.let {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(text = it, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                    }
                }
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
