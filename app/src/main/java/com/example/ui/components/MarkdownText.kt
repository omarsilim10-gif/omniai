package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onCopyCode: (String) -> Unit = {}
) {
    val blocks = splitIntoBlocks(text)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is TextBlock.Code -> {
                    CodeSnippetBlock(
                        language = block.language,
                        code = block.content,
                        onCopyCode = onCopyCode
                    )
                }
                is TextBlock.Header -> {
                    Text(
                        text = block.content,
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            2 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            else -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is TextBlock.Paragraph -> {
                    val annotated = buildInlineFormattedText(block.content, textColor, MaterialTheme.colorScheme.primary)
                    Text(
                        text = annotated,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeSnippetBlock(
    language: String,
    code: String,
    onCopyCode: (String) -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), shape)
    ) {
        // Code header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.ifBlank { "code" },
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            IconButton(
                onClick = { onCopyCode(code) },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "نسخ الكود",
                    tint = Color(0xFF94A3B8)
                )
            }
        }

        // Code content
        Text(
            text = code.trim(),
            modifier = Modifier.padding(12.dp),
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        )
    }
}

private sealed class TextBlock {
    data class Code(val language: String, val content: String) : TextBlock()
    data class Header(val level: Int, val content: String) : TextBlock()
    data class Paragraph(val content: String) : TextBlock()
}

private fun splitIntoBlocks(text: String): List<TextBlock> {
    val list = mutableListOf<TextBlock>()
    val lines = text.split("\n")
    var inCode = false
    var codeLang = ""
    val codeBuilder = StringBuilder()
    val paraBuilder = StringBuilder()

    fun flushPara() {
        if (paraBuilder.isNotBlank()) {
            list.add(TextBlock.Paragraph(paraBuilder.toString().trim()))
            paraBuilder.clear()
        }
    }

    for (line in lines) {
        if (line.trim().startsWith("```")) {
            if (inCode) {
                list.add(TextBlock.Code(codeLang, codeBuilder.toString()))
                codeBuilder.clear()
                inCode = false
            } else {
                flushPara()
                inCode = true
                codeLang = line.trim().removePrefix("```").trim()
            }
            continue
        }

        if (inCode) {
            codeBuilder.append(line).append("\n")
            continue
        }

        if (line.startsWith("### ")) {
            flushPara()
            list.add(TextBlock.Header(3, line.removePrefix("### ").trim()))
        } else if (line.startsWith("## ")) {
            flushPara()
            list.add(TextBlock.Header(2, line.removePrefix("## ").trim()))
        } else if (line.startsWith("# ")) {
            flushPara()
            list.add(TextBlock.Header(1, line.removePrefix("# ").trim()))
        } else {
            paraBuilder.append(line).append("\n")
        }
    }

    if (inCode) {
        list.add(TextBlock.Code(codeLang, codeBuilder.toString()))
    }
    flushPara()

    return list
}

private fun buildInlineFormattedText(content: String, defaultColor: Color, highlightColor: Color) = buildAnnotatedString {
    var i = 0
    val len = content.length

    while (i < len) {
        // Bold: **text**
        if (i + 1 < len && content[i] == '*' && content[i + 1] == '*') {
            val end = content.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                    append(content.substring(i + 2, end))
                }
                i = end + 2
                continue
            }
        }
        // Inline code: `code`
        if (content[i] == '`') {
            val end = content.indexOf('`', i + 1)
            if (end != -1) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = highlightColor,
                        background = Color(0x2238BDF8)
                    )
                ) {
                    append(" ${content.substring(i + 1, end)} ")
                }
                i = end + 1
                continue
            }
        }
        append(content[i])
        i++
    }
}
