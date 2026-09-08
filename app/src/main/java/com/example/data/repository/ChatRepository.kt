package com.example.data.repository

import com.example.data.local.ConversationDao
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    suspend fun getMessagesList(conversationId: Long): List<MessageEntity> {
        return messageDao.getMessagesList(conversationId)
    }

    suspend fun createConversation(title: String, mode: String = "FAST"): Long {
        val entity = ConversationEntity(
            title = title,
            mode = mode,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return conversationDao.insertConversation(entity)
    }

    suspend fun updateConversationTitle(id: Long, newTitle: String) {
        conversationDao.updateTitle(id, newTitle)
    }

    suspend fun togglePin(id: Long, currentPinned: Boolean) {
        conversationDao.updatePinned(id, !currentPinned)
    }

    suspend fun deleteConversation(id: Long) {
        messageDao.deleteMessagesForConversation(id)
        conversationDao.deleteConversationById(id)
    }

    suspend fun deleteAllConversations() {
        conversationDao.deleteAllConversations()
    }

    suspend fun insertMessage(message: MessageEntity): Long {
        val id = messageDao.insertMessage(message)
        conversationDao.updateTimestamp(message.conversationId)
        return id
    }

    suspend fun updateMessageContent(messageId: Long, newContent: String) {
        messageDao.updateMessageContent(messageId, newContent)
    }

    suspend fun deleteMessage(messageId: Long) {
        messageDao.deleteMessageById(messageId)
    }
}
