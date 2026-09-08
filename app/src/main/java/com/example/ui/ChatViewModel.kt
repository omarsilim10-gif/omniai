package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiExecutionResult
import com.example.data.ai.AiManager
import com.example.data.local.AppDatabase
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import com.example.data.model.AttachmentItem
import com.example.data.model.AttachmentType
import com.example.data.pref.AppConfig
import com.example.data.pref.AppSettings
import com.example.data.pref.AppThemeMode
import com.example.data.pref.NetworkPreference
import com.example.data.repository.ChatRepository
import com.example.util.VoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VideoScene(
    val sceneNumber: Int,
    val title: String,
    val description: String,
    val cameraAngle: String,
    val lighting: String,
    val durationSeconds: Int = 5
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = ChatRepository(db.conversationDao(), db.messageDao())
    val settings = AppSettings(application)
    private val aiManager = AiManager(application, settings)
    val voiceManager = VoiceManager(application)

    val config: StateFlow<AppConfig> = settings.config

    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    private var messagesJob: Job? = null

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isThinkingMode = MutableStateFlow(false)
    val isThinkingMode: StateFlow<Boolean> = _isThinkingMode.asStateFlow()

    private val _editingMessage = MutableStateFlow<MessageEntity?>(null)
    val editingMessage: StateFlow<MessageEntity?> = _editingMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showStudioDialog = MutableStateFlow(false)
    val showStudioDialog: StateFlow<Boolean> = _showStudioDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _studioTab = MutableStateFlow(0) // 0 = Image, 1 = Video
    val studioTab: StateFlow<Int> = _studioTab.asStateFlow()

    private val _studioPrompt = MutableStateFlow("")
    val studioPrompt: StateFlow<String> = _studioPrompt.asStateFlow()

    private val _studioStyle = MutableStateFlow("واقعي سينمائي (Photorealistic)")
    val studioStyle: StateFlow<String> = _studioStyle.asStateFlow()

    private val _studioAspectRatio = MutableStateFlow("1:1")
    val studioAspectRatio: StateFlow<String> = _studioAspectRatio.asStateFlow()

    private val _studioResult = MutableStateFlow<AiExecutionResult?>(null)
    val studioResult: StateFlow<AiExecutionResult?> = _studioResult.asStateFlow()

    private val _isStudioLoading = MutableStateFlow(false)
    val isStudioLoading: StateFlow<Boolean> = _isStudioLoading.asStateFlow()

    private val _videoScenes = MutableStateFlow<List<VideoScene>>(emptyList())
    val videoScenes: StateFlow<List<VideoScene>> = _videoScenes.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Attachment State
    private val _currentAttachment = MutableStateFlow<AttachmentItem?>(null)
    val currentAttachment: StateFlow<AttachmentItem?> = _currentAttachment.asStateFlow()

    private val _showAttachmentSheet = MutableStateFlow(false)
    val showAttachmentSheet: StateFlow<Boolean> = _showAttachmentSheet.asStateFlow()

    private val _showPackagePicker = MutableStateFlow(false)
    val showPackagePicker: StateFlow<Boolean> = _showPackagePicker.asStateFlow()

    // Studio model selection
    private val _studioImageModel = MutableStateFlow("Nano banana pro 🍌")
    val studioImageModel: StateFlow<String> = _studioImageModel.asStateFlow()

    private val _studioNegativePrompt = MutableStateFlow("")
    val studioNegativePrompt: StateFlow<String> = _studioNegativePrompt.asStateFlow()

    private val _studioResolution = MutableStateFlow("1K")
    val studioResolution: StateFlow<String> = _studioResolution.asStateFlow()

    private val _videoCameraMotion = MutableStateFlow("360° Drone Sweep (دوران درون بانورامي)")
    val videoCameraMotion: StateFlow<String> = _videoCameraMotion.asStateFlow()

    private val _videoFps = MutableStateFlow("24 FPS Cinematic")
    val videoFps: StateFlow<String> = _videoFps.asStateFlow()

    init {
        viewModelScope.launch {
            conversations.collect { list ->
                if (_activeConversation.value == null && list.isNotEmpty()) {
                    selectConversation(list.first())
                }
            }
        }
    }

    fun selectConversation(conversation: ConversationEntity) {
        _activeConversation.value = conversation
        _isThinkingMode.value = (conversation.mode == "THINKING")
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            repository.getMessagesForConversation(conversation.id).collect {
                _messages.value = it
            }
        }
    }

    fun startNewChat() {
        _activeConversation.value = null
        _messages.value = emptyList()
        _editingMessage.value = null
        _inputText.value = ""
        voiceManager.stopSpeaking()
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun toggleThinkingMode() {
        _isThinkingMode.value = !_isThinkingMode.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun sendMessage(customPrompt: String? = null) {
        val textToSend = (customPrompt ?: _inputText.value).trim()
        if (textToSend.isBlank() || _isLoading.value) return

        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            var currentConv = _activeConversation.value
            val isFirstMessage = currentConv == null

            if (currentConv == null) {
                // Auto generate title or initial title
                val isAr = config.value.language == "ar" || textToSend.any { it in '\u0600'..'\u06FF' }
                val initialTitle = if (isAr) "محادثة جارية..." else "Chatting..."
                val convId = repository.createConversation(
                    title = initialTitle,
                    mode = if (_isThinkingMode.value) "THINKING" else "FAST"
                )
                val newConv = ConversationEntity(
                    id = convId,
                    title = initialTitle,
                    mode = if (_isThinkingMode.value) "THINKING" else "FAST"
                )
                _activeConversation.value = newConv
                currentConv = newConv

                // Subscribe to messages
                messagesJob?.cancel()
                messagesJob = launch {
                    repository.getMessagesForConversation(convId).collect {
                        _messages.value = it
                    }
                }
            }

            val convId = currentConv.id

            // Insert User Message
            val userMsg = MessageEntity(
                conversationId = convId,
                role = "user",
                content = textToSend,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMessage(userMsg)

            // Auto Generate AI Title in background if this is the first message
            if (isFirstMessage) {
                launch {
                    val smartTitle = aiManager.generateTitle(textToSend)
                    repository.updateConversationTitle(convId, smartTitle)
                    _activeConversation.value = _activeConversation.value?.copy(title = smartTitle)
                }
            }

            // Build history turns
            val history = _messages.value.map { it.role to it.content }

            // Execute AI Response
            val aiResult = aiManager.generateChatResponse(
                prompt = textToSend,
                history = history,
                isThinkingMode = _isThinkingMode.value
            )

            // Save Assistant Message
            val assistantMsg = MessageEntity(
                conversationId = convId,
                role = "assistant",
                content = aiResult.responseText,
                thinkingProcess = aiResult.thinkingProcess,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMessage(assistantMsg)

            _isLoading.value = false
        }
    }

    fun startEditingMessage(message: MessageEntity) {
        _editingMessage.value = message
        _inputText.value = message.content
    }

    fun cancelEditingMessage() {
        _editingMessage.value = null
        _inputText.value = ""
    }

    fun submitEditMessage() {
        val msg = _editingMessage.value ?: return
        val newContent = _inputText.value.trim()
        if (newContent.isBlank()) return

        viewModelScope.launch {
            repository.updateMessageContent(msg.id, newContent)
            _editingMessage.value = null
            _inputText.value = ""

            // If user edited their own message, re-generate assistant answer for the edited query!
            if (msg.role == "user") {
                _isLoading.value = true
                val history = _messages.value.filter { it.id < msg.id }.map { it.role to it.content }
                val aiResult = aiManager.generateChatResponse(
                    prompt = newContent,
                    history = history,
                    isThinkingMode = _isThinkingMode.value
                )
                val newAssistantMsg = MessageEntity(
                    conversationId = msg.conversationId,
                    role = "assistant",
                    content = aiResult.responseText,
                    thinkingProcess = aiResult.thinkingProcess,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMessage(newAssistantMsg)
                _isLoading.value = false
            }
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun deleteConversation(convId: Long) {
        viewModelScope.launch {
            if (_activeConversation.value?.id == convId) {
                startNewChat()
            }
            repository.deleteConversation(convId)
        }
    }

    fun renameConversation(convId: Long, newTitle: String) {
        viewModelScope.launch {
            repository.updateConversationTitle(convId, newTitle.trim())
            if (_activeConversation.value?.id == convId) {
                _activeConversation.value = _activeConversation.value?.copy(title = newTitle.trim())
            }
        }
    }

    fun togglePin(conv: ConversationEntity) {
        viewModelScope.launch {
            repository.togglePin(conv.id, conv.isPinned)
        }
    }

    fun speakMessage(message: MessageEntity) {
        voiceManager.speak(message.content, message.id)
    }

    fun startVoiceRecognition() {
        voiceManager.startListening(
            onResult = { recognizedText ->
                _inputText.value = if (_inputText.value.isBlank()) recognizedText else "${_inputText.value} $recognizedText"
            },
            onError = { error ->
                _toastMessage.value = error
            }
        )
    }

    fun stopVoiceRecognition() {
        voiceManager.stopListening()
    }

    fun copyToClipboard(text: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("AI Message", text)
        clipboard?.setPrimaryClip(clip)
        val isAr = config.value.language == "ar"
        _toastMessage.value = if (isAr) "تم النسخ إلى الحافظة بنجاح" else "Copied to clipboard"
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Studio controls
    fun openStudio(tab: Int = 0) {
        _studioTab.value = tab
        _showStudioDialog.value = true
    }

    fun closeStudio() {
        _showStudioDialog.value = false
    }

    fun setStudioTab(tab: Int) {
        _studioTab.value = tab
    }

    fun setStudioPrompt(prompt: String) {
        _studioPrompt.value = prompt
    }

    fun setStudioStyle(style: String) {
        _studioStyle.value = style
    }

    fun setStudioAspectRatio(ratio: String) {
        _studioAspectRatio.value = ratio
    }

    fun generateStudioImage() {
        val prompt = _studioPrompt.value.trim()
        if (prompt.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        _studioResult.value = null

        viewModelScope.launch {
            val res = aiManager.generateImage(prompt, _studioStyle.value, _studioAspectRatio.value)
            _studioResult.value = res
            _isStudioLoading.value = false
        }
    }

    fun generateStudioVideoStoryboard() {
        val prompt = _studioPrompt.value.trim()
        if (prompt.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true

        viewModelScope.launch {
            val isAr = config.value.language == "ar" || prompt.any { it in '\u0600'..'\u06FF' }
            val scenes = if (isAr) {
                listOf(
                    VideoScene(1, "المشهد الافتتاحي (Establishing Shot)", "لقطة بانورامية واسعة تعرض محيط: $prompt بألوان سينمائية وتدرج ضوئي مذهل.", "Wide Drone Orbit", "Golden Hour Cinematic", 5),
                    VideoScene(2, "لقطة التفاصيل والتركيز (Medium Shot)", "حركة كاميرا سلسة تقترب من العناصر المركزية لفكرة: $prompt مع عمق ميداني ناعم.", "Slow Dolly In", "Volumetric Fog Lighting", 6),
                    VideoScene(3, "ذروة المشهد والحركة (Dynamic Close-up)", "تسارع خفيف وزوايا حيوية تبرز التفاصيل الدقيقة مع حركة ضبابية طبيعية 24fps.", "Low Angle Sweep", "Dramatic Rim Light", 5),
                    VideoScene(4, "الخاتمة السينمائية (Cinematic Outro)", "ارتفاع تدريجي للكاميرا مع تراجع بطيء لإبراز الأثر الشامل للعمل الفني.", "High Crane Pull Back", "Soft Twilight Glow", 4)
                )
            } else {
                listOf(
                    VideoScene(1, "Establishing Scene", "Wide panoramic establishing shot framing $prompt with cinematic volumetric lighting.", "Wide Drone Orbit", "Golden Hour Cinematic", 5),
                    VideoScene(2, "Detail & Subject Focus", "Smooth camera track approaching core subject of $prompt with shallow depth of field.", "Slow Dolly In", "Volumetric Fog Lighting", 6),
                    VideoScene(3, "Dynamic Climax", "Kinetic low-angle perspective emphasizing movement and high fidelity rendering.", "Low Angle Sweep", "Dramatic Rim Light", 5),
                    VideoScene(4, "Cinematic Outro", "Gradual upward tilt with smooth pull-back revealing complete scene scale.", "High Crane Pull Back", "Twilight Glow", 4)
                )
            }
            _videoScenes.value = scenes
            _isStudioLoading.value = false
        }
    }

    fun insertStudioResultToChat() {
        val result = _studioResult.value
        val prompt = _studioPrompt.value
        if (result != null) {
            val text = "🎨 **${_studioStyle.value} (${_studioAspectRatio.value})**\n\n${result.responseText}"
            sendMessage(text)
            _showStudioDialog.value = false
        }
    }

    // Settings
    fun openSettings() {
        _showSettingsDialog.value = true
    }

    fun closeSettings() {
        _showSettingsDialog.value = false
    }

    fun setTheme(theme: AppThemeMode) {
        settings.setTheme(theme)
    }

    fun setLanguage(lang: String) {
        settings.setLanguage(lang)
    }

    fun setApiKey(key: String) {
        settings.setApiKey(key)
    }

    fun setCustomBaseUrl(url: String) {
        settings.setCustomBaseUrl(url)
    }

    fun setNetworkPreference(pref: NetworkPreference) {
        settings.setNetworkPreference(pref)
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllConversations()
            startNewChat()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
