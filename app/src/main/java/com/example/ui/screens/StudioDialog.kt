package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.AiExecutionResult
import com.example.ui.VideoScene
import com.example.ui.components.MarkdownText
import com.example.ui.theme.AmberThinking
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioDialog(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    prompt: String,
    onPromptChanged: (String) -> Unit,
    style: String,
    onStyleSelected: (String) -> Unit,
    aspectRatio: String,
    onAspectRatioSelected: (String) -> Unit,
    generatedResult: AiExecutionResult?,
    videoScenes: List<VideoScene>,
    isLoading: Boolean,
    onGenerateImage: () -> Unit,
    onGenerateVideoStoryboard: () -> Unit,
    onInsertToChat: () -> Unit,
    onInsertVideoToChat: () -> Unit = {},
    onCopyPrompt: (String) -> Unit,
    onDismiss: () -> Unit,
    isArabic: Boolean,
    selectedImageModel: String = "Nano banana pro 🍌",
    onSelectImageModel: (String) -> Unit = {},
    negativePrompt: String = "",
    onNegativePromptChanged: (String) -> Unit = {},
    resolution: String = "1K",
    onResolutionSelected: (String) -> Unit = {},
    cameraMotion: String = "360° Drone Sweep",
    onCameraMotionSelected: (String) -> Unit = {},
    videoFps: String = "24 FPS Cinematic",
    onVideoFpsSelected: (String) -> Unit = {}
) {
    val imageModels = listOf(
        "Nano banana pro 🍌",
        "Imagen 3 Ultra 🎨",
        "Flux 1.1 Pro ⚡"
    )

    val styles = listOf(
        "واقعي سينمائي (Photorealistic)",
        "أنمي فانتزي (Anime Fantasy)",
        "سايبربانك مستقبلي (Cyberpunk Neon)",
        "رندر ثلاثي الأبعاد (3D Octane)",
        "لوحة زيتية (Oil Painting)",
        "فيكتور حديث (Modern Vector)"
    )

    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3", "21:9")
    val resolutions = listOf("1K", "2K", "4K Ultra HD")

    val cameraMotions = listOf(
        "360° Drone Sweep (دوران درون بانورامي)",
        "Slow Dolly Zoom (زوم سينمائي ناعم)",
        "Dynamic FPV Dive (انقضاض درون FPV سريع)",
        "Smooth Tracking Shot (تتبع سينمائي سلس)",
        "Golden Hour Pan (تحريك بألوان الغروب)"
    )

    val sampleImagePrompts = if (isArabic) {
        listOf(
            "رائد فضاء يسبح في سديم فضائي مشع ونجوم متلألئة",
            "مدينة مستقبلية عائمة في سماء بنفسجية مع قطارات طائرة",
            "واحة عربية بتقنية السايبربانك وتدرج نيون مذهل"
        )
    } else {
        listOf(
            "Astronaut floating through a radiant cosmic nebula with glowing stardust",
            "Futuristic floating metropolis in a violet sky with aerial transit",
            "Cyberpunk oasis under neon auroras and dramatic cinematic rim light"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(22.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                                .background(Brush.linearGradient(listOf(AmberThinking, NeonViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "استوديو الصور والفيديو بالذكاء الاصطناعي" else "AI Generative Media Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "Nano banana pro • Veo 3.1 Cinema" else "Nano banana pro & Veo 3.1 Pro",
                                fontSize = 11.sp,
                                color = AmberThinking,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_studio_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Image vs Video
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { onTabSelected(0) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "توليد الصور (Nano banana pro)" else "Image (Nano banana)")
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "صناعة الفيديو احترافياً (Veo)" else "Pro Video Studio")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // TAB 0: Image Generation (Nano Banana Pro)
                    if (selectedTab == 0) {
                        // Model Selector
                        Text(
                            text = if (isArabic) "اختر نموذج التوليد:" else "Select AI Generation Model:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            imageModels.forEach { model ->
                                val isSelected = model == selectedImageModel
                                val isBanana = model.contains("banana")
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) {
                                                if (isBanana) Brush.linearGradient(listOf(AmberThinking, Color(0xFFD97706)))
                                                else Brush.linearGradient(listOf(NeonViolet, ElectricCyan))
                                            } else {
                                                Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) AmberThinking else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectImageModel(model) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("model_${model.take(5)}")
                                ) {
                                    Text(
                                        text = model,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Prompt Input
                        Text(
                            text = if (isArabic) "وصف الصورة الفنية المراد توليدها:" else "Image Prompt:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = prompt,
                            onValueChange = onPromptChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("studio_prompt_input"),
                            placeholder = {
                                Text(
                                    if (isArabic) "مثال: رائد فضاء عربي يكتشف واحة كريستالية على كوكب المريخ بألوان سينمائية..."
                                    else "E.g. Astronaut discovering an illuminated crystal oasis on Mars with cinematic volumetric lighting..."
                                )
                            },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberThinking,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        // Negative Prompt
                        Text(
                            text = if (isArabic) "عناصر مستبعدة (Negative Prompt - اختياري):" else "Negative Prompt (Optional):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = negativePrompt,
                            onValueChange = onNegativePromptChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("studio_negative_prompt_input"),
                            placeholder = {
                                Text(
                                    text = if (isArabic) "تشويش، جودة منخفضة، أطراف زائدة، نصوص غير مرغوبة..."
                                    else "Blurry, low resolution, artifacts, distorted hands...",
                                    fontSize = 12.sp
                                )
                            },
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Suggestion Chips
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            sampleImagePrompts.forEach { sample ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                        .clickable { onPromptChanged(sample) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = sample, fontSize = 11.sp, color = AmberThinking, maxLines = 1)
                                }
                            }
                        }

                        // Styles
                        Text(text = if (isArabic) "النمط البصري:" else "Visual Style:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            styles.forEach { st ->
                                val isSelected = st == style
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) AmberThinking else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .clickable { onStyleSelected(st) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = st,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Aspect Ratio & Resolution Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = if (isArabic) "نسبة الأبعاد:" else "Aspect Ratio:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    aspectRatios.forEach { ratio ->
                                        val isSelected = ratio == aspectRatio
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .clickable { onAspectRatioSelected(ratio) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = ratio,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = if (isArabic) "الدقة:" else "Resolution:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    resolutions.forEach { res ->
                                        val isSelected = res == resolution
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) NeonViolet else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .clickable { onResolutionSelected(res) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = res,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Generate Image Button
                        Button(
                            onClick = onGenerateImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("generate_image_btn"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = prompt.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberThinking)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF031024), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isArabic) "جاري التوليد بـ Nano banana pro..." else "Generating via Nano banana pro...", color = Color(0xFF031024))
                            } else {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF031024))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isArabic) "توليد الصورة بـ Nano banana pro 🍌" else "Generate with Nano Banana Pro 🍌", color = Color(0xFF031024), fontWeight = FontWeight.Bold)
                            }
                        }

                        // Image Result Preview
                        generatedResult?.let { res ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AmberThinking.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    res.generatedImageUrl?.let { uri ->
                                        if (uri.startsWith("data:image")) {
                                            val base64Data = uri.substringAfter("base64,")
                                            val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                            if (bitmap != null) {
                                                Image(
                                                    bitmap = bitmap.asImageBitmap(),
                                                    contentDescription = "Generated AI Image",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                    }

                                    MarkdownText(text = res.responseText)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = onInsertToChat,
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberThinking),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("insert_image_to_chat_btn")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF031024), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isArabic) "إدراج بالمحادثة" else "Insert to Chat", color = Color(0xFF031024), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        // TAB 1: Professional Video Generator (Veo 3.1 Pro Cinema)
                        Text(
                            text = if (isArabic) "فكرة وسيناريو الفيديو الاحترافي:" else "Cinematic Video Concept:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = prompt,
                            onValueChange = onPromptChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("studio_video_prompt_input"),
                            placeholder = {
                                Text(
                                    if (isArabic) "مثال: هبوط مركبة فضائية عملاقة بين ناطحات سحاب في عاصفة رعدية نيونية مع حركة درون سريعة..."
                                    else "E.g. Giant spacecraft descending between neon skyscrapers in thunderstorm with FPV drone dive..."
                                )
                            },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        // Camera Motion Rig
                        Text(
                            text = if (isArabic) "حركة الكاميرا والإخراج السينمائي:" else "Camera Motion & Director Rig:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            cameraMotions.forEach { motion ->
                                val isSelected = motion == cameraMotion
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonViolet else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .clickable { onCameraMotionSelected(motion) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = motion,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // FPS & Quality Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("24 FPS Cinematic", "60 FPS Ultra Fluid", "4K ProRes HDR").forEach { fps ->
                                val isSelected = fps == videoFps
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .clickable { onVideoFpsSelected(fps) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = fps,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Live Video Preview Simulation Canvas
                        var isPreviewPlaying by remember { mutableStateOf(false) }
                        val previewTransition = rememberInfiniteTransition(label = "preview_progress")
                        val previewProgress by previewTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(10000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "preview_prog"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.radialGradient(listOf(Color(0xFF2E1065), Color(0xFF030712)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🎥 GOOGLE VEO 3.1 CINEMA ENGINE",
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isPreviewPlaying) "▶️ جاري العرض السينمائي المباشر..." else "⏸️ معاينة المشهد وحركة الكاميرا",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = cameraMotion,
                                    color = NeonViolet,
                                    fontSize = 10.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NeonViolet.copy(alpha = 0.85f))
                                    .clickable { isPreviewPlaying = !isPreviewPlaying },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPreviewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "$videoFps • Veo 3.1", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            if (isPreviewPlaying) {
                                LinearProgressIndicator(
                                    progress = { previewProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .height(4.dp),
                                    color = NeonViolet,
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }

                        // Generate Storyboard & Video
                        Button(
                            onClick = onGenerateVideoStoryboard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("generate_video_btn"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = prompt.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isArabic) "جاري إخراج مشاهد الفيديو السينمائي..." else "Directing Video Storyboard...", color = Color.White)
                            } else {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isArabic) "صناعة وإخراج الفيديو الآن 🎬" else "Generate & Direct Video 🎬", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Storyboard Cards
                        if (videoScenes.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "مشاهد الإخراج السينمائي (${videoScenes.size} مشاهد):" else "Cinematic Storyboard (${videoScenes.size} scenes):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonViolet
                                )

                                Button(
                                    onClick = onInsertVideoToChat,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("insert_video_to_chat_btn")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF031024), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "إدراج الفيديو بالمحادثة" else "Insert Video to Chat", color = Color(0xFF031024), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            videoScenes.forEach { sc ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, NeonViolet.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "مشهد #${sc.sceneNumber}: ${sc.title}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = NeonViolet
                                            )
                                            Text(
                                                text = "${sc.durationSeconds} ثوانٍ",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = sc.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(text = "🎥 ${sc.cameraAngle}", fontSize = 11.sp, color = ElectricCyan)
                                            Text(text = "💡 ${sc.lighting}", fontSize = 11.sp, color = AmberThinking)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
