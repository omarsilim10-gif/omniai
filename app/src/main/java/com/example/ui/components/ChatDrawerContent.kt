package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.data.pref.AppConfig
import com.example.data.pref.AppThemeMode
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet

@Composable
fun ChatDrawerContent(
    conversations: List<ConversationEntity>,
    activeConversation: ConversationEntity?,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onSelectConversation: (ConversationEntity) -> Unit,
    onNewChat: () -> Unit,
    onRenameConversation: (Long, String) -> Unit,
    onTogglePin: (ConversationEntity) -> Unit,
    onDeleteConversation: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    config: AppConfig,
    onToggleTheme: (AppThemeMode) -> Unit,
    onToggleLanguage: (String) -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var conversationToRename by remember { mutableStateOf<ConversationEntity?>(null) }
    var renameDialogText by remember { mutableStateOf("") }
    var showClearAllConfirm by remember { mutableStateOf(false) }

    val filteredConversations = remember(conversations, searchQuery) {
        if (searchQuery.isBlank()) conversations
        else conversations.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // App Branding Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(ElectricCyan, NeonViolet))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "OmniAI Studio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (config.language == "ar") "مساعدك الذكي الشامل" else "Your Omnipotent AI Companion",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // New Chat Button
        Button(
            onClick = onNewChat,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("new_chat_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricCyan
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF031024))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (config.language == "ar") "محادثة جديدة" else "New Chat",
                color = Color(0xFF031024),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_conversations_input"),
            placeholder = {
                Text(
                    text = if (config.language == "ar") "بحث في المحادثات..." else "Search chats...",
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Conversation List
        Text(
            text = if (config.language == "ar") "المحادثات السابقة" else "Recent Chats",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (filteredConversations.isEmpty()) {
                item {
                    Text(
                        text = if (config.language == "ar") "لا توجد محادثات بعد" else "No chats yet",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp)
                    )
                }
            } else {
                items(filteredConversations, key = { it.id }) { conv ->
                    val isSelected = activeConversation?.id == conv.id
                    ConversationDrawerItem(
                        conversation = conv,
                        isSelected = isSelected,
                        onSelect = { onSelectConversation(conv) },
                        onRename = {
                            conversationToRename = conv
                            renameDialogText = conv.title
                        },
                        onTogglePin = { onTogglePin(conv) },
                        onDelete = { onDeleteConversation(conv.id) }
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Bottom Drawer Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Toggle
            IconButton(
                onClick = {
                    val next = if (config.themeMode == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
                    onToggleTheme(next)
                },
                modifier = Modifier.testTag("theme_toggle_btn")
            ) {
                Icon(
                    imageVector = if (config.themeMode == AppThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "تبديل المظهر",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Language Toggle
            IconButton(
                onClick = {
                    val nextLang = if (config.language == "ar") "en" else "ar"
                    onToggleLanguage(nextLang)
                },
                modifier = Modifier.testTag("lang_toggle_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = "تغيير اللغة", tint = NeonViolet)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (config.language == "ar") "EN" else "عربي",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonViolet
                    )
                }
            }

            // Clear All Data
            IconButton(
                onClick = { showClearAllConfirm = true },
                modifier = Modifier.testTag("clear_all_chats_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "مسح المحادثات",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Settings
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("drawer_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Rename Conversation Dialog
    conversationToRename?.let { conv ->
        AlertDialog(
            onDismissRequest = { conversationToRename = null },
            title = { Text(text = if (config.language == "ar") "تعديل عنوان المحادثة" else "Rename Chat") },
            text = {
                OutlinedTextField(
                    value = renameDialogText,
                    onValueChange = { renameDialogText = it },
                    modifier = Modifier.fillMaxWidth().testTag("rename_input_field"),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameDialogText.isNotBlank()) {
                            onRenameConversation(conv.id, renameDialogText.trim())
                        }
                        conversationToRename = null
                    },
                    modifier = Modifier.testTag("confirm_rename_btn")
                ) {
                    Text(if (config.language == "ar") "حفظ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { conversationToRename = null }) {
                    Text(if (config.language == "ar") "إلغاء" else "Cancel")
                }
            }
        )
    }

    // Clear All Dialog
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text(if (config.language == "ar") "مسح جميع المحادثات" else "Clear All Chats") },
            text = {
                Text(
                    if (config.language == "ar")
                        "هل أنت متأكد من رغبتك في حذف كافة المحادثات والرسائل؟ لا يمكن التراجع عن هذا الإجراء."
                    else
                        "Are you sure you want to delete all conversations and messages? This cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearAllConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_clear_all_btn")
                ) {
                    Text(if (config.language == "ar") "مسح الكل" else "Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text(if (config.language == "ar") "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun ConversationDrawerItem(
    conversation: ConversationEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else Color.Transparent
            )
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("conv_item_${conversation.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (conversation.isPinned) Icons.Default.PushPin else Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            tint = if (conversation.isPinned) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = conversation.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(24.dp).testTag("conv_menu_btn_${conversation.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "خيارات",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (conversation.isPinned) "إلغاء التثبيت" else "تثبيت في الأعلى") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (conversation.isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        showMenu = false
                        onTogglePin()
                    }
                )
                DropdownMenuItem(
                    text = { Text("تعديل الاسم") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onRename()
                    }
                )
                DropdownMenuItem(
                    text = { Text("حذف المحادثة", color = Color(0xFFEF4444)) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}
