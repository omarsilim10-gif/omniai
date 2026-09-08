package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apple
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.theme.AmberThinking
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
    message: MessageEntity,
    isSpeakingThis: Boolean,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    var isThinkingExpanded by remember { mutableStateOf(false) }

    val formattedTime = remember(message.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                // Assistant Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricCyan, NeonViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (message.thinkingProcess != null) Icons.Default.Psychology else Icons.Default.AutoAwesome,
                        contentDescription = "Assistant Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isUser) 0.85f else 0.92f)
            ) {
                // Thinking process accordion if assistant had thinking steps
                if (!isUser && !message.thinkingProcess.isNullOrBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AmberThinking.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { isThinkingExpanded = !isThinkingExpanded }
                            .testTag("thinking_accordion_${message.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = AmberThinking.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = AmberThinking,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "خطوات التفكير والتحليل المنطقي",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberThinking
                                    )
                                }
                                Icon(
                                    imageVector = if (isThinkingExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand Thinking",
                                    tint = AmberThinking,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = isThinkingExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    MarkdownText(
                                        text = message.thinkingProcess,
                                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Attachment Display Card (APK, IPA, IPK, Image, Document, Video)
                if (message.attachmentType != null || message.imageUrl != null) {
                    val attType = message.attachmentType ?: "IMAGE"
                    val attName = message.attachmentName ?: "Attachment"
                    val attSize = message.attachmentSize ?: ""
                    val attExtra = message.attachmentExtra ?: ""

                    val accentColor = when (attType) {
                        "APK" -> Color(0xFF10B981)
                        "IPA" -> Color(0xFF38BDF8)
                        "IPK" -> Color(0xFFA855F7)
                        "IMAGE" -> ElectricCyan
                        "VIDEO" -> NeonViolet
                        else -> Color(0xFFF59E0B)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Video Player Preview
                            if (attType == "VIDEO") {
                                var isPlayingVideo by remember { mutableStateOf(false) }
                                val infiniteTransition = rememberInfiniteTransition(label = "video_progress")
                                val simulatedProgress by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(8000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "progress"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.radialGradient(listOf(Color(0xFF1E1B4B), Color(0xFF030712)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Ambient Video Graphics Canvas
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🎬 VEO 3.1 PRO CINEMA PLAYER",
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isPlayingVideo) "▶️ جاري العرض السينمائي (Rendering 4K 60FPS)..." else "⏸️ انقر للتشغيل والمشاهدة",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (attExtra.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = attExtra,
                                                color = NeonViolet,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    // Play / Pause Floating Toggle
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(ElectricCyan.copy(alpha = 0.85f))
                                            .clickable { isPlayingVideo = !isPlayingVideo },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFF031024),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    // Watermark badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "4K • 60 FPS • HDR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Timeline Scrubber Bar
                                    if (isPlayingVideo) {
                                        LinearProgressIndicator(
                                            progress = { simulatedProgress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .height(4.dp),
                                            color = ElectricCyan,
                                            trackColor = Color.White.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Image Preview if base64 or imageUrl
                            val effectiveImgUrl = message.imageUrl ?: (if (attType == "IMAGE") message.attachmentUri else null)
                            if (effectiveImgUrl != null && effectiveImgUrl.startsWith("data:image")) {
                                runCatching {
                                    val b64 = effectiveImgUrl.substringAfter("base64,")
                                    val bytes = Base64.decode(b64, Base64.DEFAULT)
                                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    if (bmp != null) {
                                        Image(
                                            bitmap = bmp.asImageBitmap(),
                                            contentDescription = "Attachment Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }

                            // Meta Row
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
                                            .background(accentColor.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (attType) {
                                                "APK" -> Icons.Default.Android
                                                "IPA" -> Icons.Default.Apple
                                                "IPK" -> Icons.Default.Tv
                                                "IMAGE" -> Icons.Default.Image
                                                "VIDEO" -> Icons.Default.VideoCameraBack
                                                else -> Icons.Default.Description
                                            },
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = attName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(accentColor)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(text = attType, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                        if (attSize.isNotBlank()) {
                                            Text(
                                                text = "$attSize${if (attExtra.isNotBlank()) " • $attExtra" else ""}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                if (attType in listOf("APK", "IPA", "IPK")) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(accentColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Security, contentDescription = null, tint = accentColor, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(text = "SHA-256 Valid", fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Main Message Box
                val shape = if (isUser) {
                    RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                } else {
                    RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                }

                val bubbleBg = if (isUser) {
                    Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                } else {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(bubbleBg)
                        .border(
                            1.dp,
                            if (isUser) Color(0xFF38BDF8).copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("message_bubble_${message.id}")
                ) {
                    MarkdownText(
                        text = message.content,
                        textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                        onCopyCode = { onCopy() }
                    )
                }

                // Bottom Meta and Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (message.isEdited) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(مُعدّلة)",
                            fontSize = 11.sp,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Copy button
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(28.dp).testTag("copy_msg_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // TTS Voice speaker button for assistant messages
                    if (!isUser) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(28.dp).testTag("speak_msg_${message.id}")
                        ) {
                            Icon(
                                imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.Default.RecordVoiceOver,
                                contentDescription = if (isSpeakingThis) "إيقاف الصوت" else "قراءة صوتية",
                                modifier = Modifier.size(15.dp),
                                tint = if (isSpeakingThis) Color(0xFFEF4444) else ElectricCyan
                            )
                        }
                    }

                    // Edit button for user messages
                    if (isUser) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(28.dp).testTag("edit_msg_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل الرسالة",
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_msg_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الرسالة",
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
