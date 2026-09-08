package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.theme.AmberThinking
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apple
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tv
import com.example.data.model.AttachmentItem
import com.example.data.model.AttachmentType

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
    isThinkingMode: Boolean,
    onToggleThinkingMode: () -> Unit,
    editingMessage: MessageEntity?,
    onCancelEdit: () -> Unit,
    onSubmitEdit: () -> Unit,
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onOpenStudio: () -> Unit,
    currentAttachment: AttachmentItem? = null,
    onRemoveAttachment: () -> Unit = {},
    onOpenAttachmentSheet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Edit Banner
            AnimatedVisibility(visible = editingMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricCyan.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✏️ تعديل الرسالة المرسلة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElectricCyan
                    )
                    Row {
                        IconButton(
                            onClick = onCancelEdit,
                            modifier = Modifier.size(28.dp).testTag("cancel_edit_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = onSubmitEdit,
                            modifier = Modifier.size(28.dp).testTag("submit_edit_btn")
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "حفظ وإعادة إرسال", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Mode Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Toggle Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isThinkingMode) AmberThinking.copy(alpha = 0.15f) else ElectricCyan.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isThinkingMode) AmberThinking.copy(alpha = 0.5f) else ElectricCyan.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onToggleThinkingMode() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("mode_toggle_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isThinkingMode) Icons.Default.Psychology else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isThinkingMode) AmberThinking else ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isThinkingMode) "وضع التفكير العميق 🧠" else "وضع الرد السريع ⚡",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isThinkingMode) AmberThinking else ElectricCyan
                    )
                }

                // Studio Shortcut Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NeonViolet.copy(alpha = 0.12f))
                        .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onOpenStudio() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("open_studio_shortcut"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoCameraBack,
                        contentDescription = "استوديو الصور والفيديو",
                        tint = NeonViolet,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "صور & فيديو 🎨",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeonViolet
                    )
                }
            }

            // Attachment Preview Banner
            AnimatedVisibility(visible = currentAttachment != null) {
                currentAttachment?.let { att ->
                    val accentColor = when (att.type) {
                        AttachmentType.APK -> Color(0xFF10B981)
                        AttachmentType.IPA -> Color(0xFF38BDF8)
                        AttachmentType.IPK -> Color(0xFFA855F7)
                        AttachmentType.IMAGE -> ElectricCyan
                        AttachmentType.DOCUMENT -> Color(0xFFF59E0B)
                        AttachmentType.VIDEO -> NeonViolet
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (att.type) {
                                        AttachmentType.APK -> Icons.Default.Android
                                        AttachmentType.IPA -> Icons.Default.Apple
                                        AttachmentType.IPK -> Icons.Default.Tv
                                        AttachmentType.IMAGE -> Icons.Default.Image
                                        AttachmentType.DOCUMENT -> Icons.Default.Description
                                        AttachmentType.VIDEO -> Icons.Default.VideoCameraBack
                                    },
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = att.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(accentColor)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = att.type.extensionTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                Text(
                                    text = "${att.sizeString}${if (att.extraInfo != null) " • ${att.extraInfo}" else ""}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onRemoveAttachment,
                            modifier = Modifier.size(24.dp).testTag("remove_attachment_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إزالة المرفق", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plus (+) Button for Attachments, Packages, Nano Banana Pro, & Video Studio
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricCyan.copy(alpha = 0.2f), NeonViolet.copy(alpha = 0.2f))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(ElectricCyan, NeonViolet)),
                            CircleShape
                        )
                        .clickable { onOpenAttachmentSheet() }
                        .testTag("open_attachment_sheet_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إرفاق صور، ملفات، حزم، أو استوديو",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Voice Input Mic Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .scale(if (isListening) micScale else 1f)
                        .clip(CircleShape)
                        .background(
                            if (isListening) Brush.radialGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C)))
                            else Brush.radialGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                        )
                        .clickable {
                            if (isListening) onStopListening() else onStartListening()
                        }
                        .testTag("voice_input_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "إيقاف التسجيل" else "تسجيل صوتي",
                        tint = if (isListening) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Text Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    placeholder = {
                        Text(
                            text = if (isListening) "جاري الاستماع لصوتك..."
                            else if (currentAttachment != null) "اكتب سؤالاً حول ${currentAttachment.name} أو اضغط إرسال..."
                            else "اسأل أي شيء، اطلب كوداً، أرفق ملفاً...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isThinkingMode) AmberThinking else ElectricCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                val canSend = (inputText.isNotBlank() || currentAttachment != null) && !isLoading

                // Send Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) {
                                Brush.linearGradient(
                                    if (isThinkingMode) listOf(AmberThinking, Color(0xFFD97706))
                                    else listOf(ElectricCyan, NeonViolet)
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                        )
                        .clickable(enabled = canSend) {
                            if (editingMessage != null) {
                                onSubmitEdit()
                            } else {
                                onSend()
                            }
                        }
                        .testTag("send_msg_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = ElectricCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (editingMessage != null) Icons.Default.Done else Icons.AutoMirrored.Filled.Send,
                            contentDescription = "إرسال",
                            tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
