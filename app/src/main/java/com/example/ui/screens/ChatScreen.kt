package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.pref.NetworkPreference
import com.example.ui.ChatViewModel
import com.example.ui.components.ChatDrawerContent
import com.example.ui.components.ChatInputBar
import com.example.ui.components.MessageItem
import com.example.ui.theme.AmberThinking
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThinkingMode by viewModel.isThinkingMode.collectAsStateWithLifecycle()
    val editingMessage by viewModel.editingMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val config by viewModel.config.collectAsStateWithLifecycle()
    val isListening by viewModel.voiceManager.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()
    val currentSpeakingId by viewModel.voiceManager.currentSpeakingId.collectAsStateWithLifecycle()

    val showStudioDialog by viewModel.showStudioDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val studioTab by viewModel.studioTab.collectAsStateWithLifecycle()
    val studioPrompt by viewModel.studioPrompt.collectAsStateWithLifecycle()
    val studioStyle by viewModel.studioStyle.collectAsStateWithLifecycle()
    val studioAspectRatio by viewModel.studioAspectRatio.collectAsStateWithLifecycle()
    val studioResult by viewModel.studioResult.collectAsStateWithLifecycle()
    val isStudioLoading by viewModel.isStudioLoading.collectAsStateWithLifecycle()
    val videoScenes by viewModel.videoScenes.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val isArabic = config.language == "ar"
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Toast listener
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    ChatDrawerContent(
                        conversations = conversations,
                        activeConversation = activeConversation,
                        searchQuery = searchQuery,
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        onSelectConversation = {
                            viewModel.selectConversation(it)
                            scope.launch { drawerState.close() }
                        },
                        onNewChat = {
                            viewModel.startNewChat()
                            scope.launch { drawerState.close() }
                        },
                        onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
                        onTogglePin = { viewModel.togglePin(it) },
                        onDeleteConversation = { viewModel.deleteConversation(it) },
                        onOpenSettings = {
                            viewModel.openSettings()
                            scope.launch { drawerState.close() }
                        },
                        config = config,
                        onToggleTheme = { viewModel.setTheme(it) },
                        onToggleLanguage = { viewModel.setLanguage(it) },
                        onClearAllData = { viewModel.clearAllData() }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = activeConversation?.title ?: if (isArabic) "OmniAI الذكي" else "OmniAI Chat",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Network status indicator badge
                                val isOfflinePref = config.networkPreference == NetworkPreference.OFFLINE_ONLY
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isOfflinePref) AmberThinking.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.2f))
                                        .clickable { viewModel.openSettings() }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isOfflinePref) Icons.Default.WifiOff else Icons.Default.Wifi,
                                            contentDescription = null,
                                            tint = if (isOfflinePref) AmberThinking else ElectricCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isOfflinePref) (if (isArabic) "بدون نت" else "Offline") else (if (isArabic) "متصل" else "Online"),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOfflinePref) AmberThinking else ElectricCyan
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("open_drawer_btn")
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "القائمة")
                            }
                        },
                        actions = {
                            // Studio Shortcut
                            IconButton(
                                onClick = { viewModel.openStudio(0) },
                                modifier = Modifier.testTag("app_bar_studio_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoCameraBack,
                                    contentDescription = "استوديو الصور والفيديو",
                                    tint = NeonViolet
                                )
                            }

                            // New Chat Action
                            IconButton(
                                onClick = { viewModel.startNewChat() },
                                modifier = Modifier.testTag("app_bar_new_chat_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "محادثة جديدة",
                                    tint = ElectricCyan
                                )
                            }

                            // Settings Action
                            IconButton(
                                onClick = { viewModel.openSettings() },
                                modifier = Modifier.testTag("app_bar_settings_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "الإعدادات",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    ChatInputBar(
                        inputText = inputText,
                        onInputChanged = { viewModel.setInputText(it) },
                        onSend = { viewModel.sendMessage() },
                        isLoading = isLoading,
                        isThinkingMode = isThinkingMode,
                        onToggleThinkingMode = { viewModel.toggleThinkingMode() },
                        editingMessage = editingMessage,
                        onCancelEdit = { viewModel.cancelEditingMessage() },
                        onSubmitEdit = { viewModel.submitEditMessage() },
                        isListening = isListening,
                        onStartListening = { viewModel.startVoiceRecognition() },
                        onStopListening = { viewModel.stopVoiceRecognition() },
                        onOpenStudio = { viewModel.openStudio(0) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (messages.isEmpty() && !isLoading) {
                        // Empty State / Welcome Screen
                        EmptyChatGreeting(
                            isArabic = isArabic,
                            onSuggestionClicked = { suggestion ->
                                viewModel.sendMessage(suggestion)
                            },
                            onOpenStudio = { viewModel.openStudio(0) }
                        )
                    } else {
                        // Message Stream
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                val isSpeakingThis = isSpeaking && currentSpeakingId == msg.id
                                MessageItem(
                                    message = msg,
                                    isSpeakingThis = isSpeakingThis,
                                    onSpeak = { viewModel.speakMessage(msg) },
                                    onCopy = { viewModel.copyToClipboard(msg.content) },
                                    onEdit = { viewModel.startEditingMessage(msg) },
                                    onDelete = { viewModel.deleteMessage(msg.id) }
                                )
                            }

                            // Loading / Thinking Indicator Bubble
                            if (isLoading) {
                                item {
                                    LoadingAssistantBubble(isThinkingMode = isThinkingMode, isArabic = isArabic)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Studio Dialog
    if (showStudioDialog) {
        StudioDialog(
            selectedTab = studioTab,
            onTabSelected = { viewModel.setStudioTab(it) },
            prompt = studioPrompt,
            onPromptChanged = { viewModel.setStudioPrompt(it) },
            style = studioStyle,
            onStyleSelected = { viewModel.setStudioStyle(it) },
            aspectRatio = studioAspectRatio,
            onAspectRatioSelected = { viewModel.setStudioAspectRatio(it) },
            generatedResult = studioResult,
            videoScenes = videoScenes,
            isLoading = isStudioLoading,
            onGenerateImage = { viewModel.generateStudioImage() },
            onGenerateVideoStoryboard = { viewModel.generateStudioVideoStoryboard() },
            onInsertToChat = { viewModel.insertStudioResultToChat() },
            onCopyPrompt = { viewModel.copyToClipboard(it) },
            onDismiss = { viewModel.closeStudio() },
            isArabic = isArabic
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            config = config,
            onSaveApiKey = { viewModel.setApiKey(it) },
            onSaveBaseUrl = { viewModel.setCustomBaseUrl(it) },
            onSetTheme = { viewModel.setTheme(it) },
            onSetLanguage = { viewModel.setLanguage(it) },
            onSetNetworkPreference = { viewModel.setNetworkPreference(it) },
            onDismiss = { viewModel.closeSettings() },
            isArabic = isArabic
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyChatGreeting(
    isArabic: Boolean,
    onSuggestionClicked: (String) -> Unit,
    onOpenStudio: () -> Unit
) {
    val starterPrompts = if (isArabic) {
        listOf(
            "💡 اشرح لي كيف تعمل نماذج التفكير العميق في الذكاء الاصطناعي؟",
            "💻 اكتب لي دالة كوتلن لمعالجة وتنظيم سلاسل النصوص",
            "🎨 صمم لي برومبت احترافي لرائد فضاء في سديم كوني",
            "🎬 اكتب سيناريو فيديو وثائقي مشوق مدته 30 ثانية",
            "🌐 كيف يمكنني ربط واجهات برمجة التطبيقات API في أندرويد؟"
        )
    } else {
        listOf(
            "💡 Explain how Deep Reasoning models function step-by-step",
            "💻 Write a robust Kotlin utility function with error handling",
            "🎨 Generate a prompt for an astronaut floating in a nebula",
            "🎬 Create a cinematic 30-second video storyboard",
            "🌐 How to integrate REST APIs in modern Android apps?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(ElectricCyan, NeonViolet))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isArabic) "أهلاً بك في OmniAI Studio" else "Welcome to OmniAI Studio",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic)
                "المساعد الذكي المتكامل للدردشة السريعة والتفكير العميق، وتوليد الصور والفيديوهات، والعمل المتصل أو بدون إنترنت."
            else
                "Your versatile AI companion for rapid responses, deep reasoning, image and video generation, working seamlessly online and offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isArabic) "اقتراحات لبدء المحادثة:" else "Quick Suggestions:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            starterPrompts.forEach { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable { onSuggestionClicked(prompt) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingAssistantBubble(isThinkingMode: Boolean, isArabic: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(ElectricCyan, NeonViolet))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isThinkingMode) Icons.Default.Psychology else Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = if (isThinkingMode) AmberThinking else ElectricCyan
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isThinkingMode) {
                        if (isArabic) "جاري التفكير العميق وتحليل الخطوات المنطقية..." else "Evaluating multi-step deep reasoning..."
                    } else {
                        if (isArabic) "جاري صياغة الرد السريع..." else "Generating rapid response..."
                    },
                    fontSize = 13.sp,
                    color = if (isThinkingMode) AmberThinking else ElectricCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
