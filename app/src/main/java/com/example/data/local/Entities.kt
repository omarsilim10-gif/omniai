package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val mode: String = "FAST" // "FAST" or "THINKING"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user" or "assistant"
    val content: String,
    val thinkingProcess: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false,
    val imageUrl: String? = null,
    val hasAudio: Boolean = false,
    val attachmentName: String? = null,
    val attachmentType: String? = null, // "image", "apk", "ipa", "ipk", "file", "video"
    val attachmentSize: String? = null,
    val attachmentUri: String? = null,
    val attachmentExtra: String? = null,
    val videoDataJson: String? = null
)
