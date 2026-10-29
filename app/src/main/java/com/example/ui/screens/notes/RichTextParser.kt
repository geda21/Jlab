package com.example.ui.screens.notes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.ui.theme.HighlightInk
import com.example.ui.theme.HighlightYellow

object RichTextParser {

    fun hexToColor(hex: String): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorLong = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(colorLong or 0xFF000000L)
            } else {
                Color(colorLong)
            }
        } catch (_: Exception) {
            HighlightYellow
        }
    }

    /**
     * Strips Markdown and HTML formatting tags to return clean plain text for search and snippets.
     */
    fun toPlainText(raw: String): String {
        return raw
            .replace(Regex("<mark[^>]*>"), "")
            .replace("</mark>", "")
            .replace("<u>", "")
            .replace("</u>", "")
            .replace("<s>", "")
            .replace("</s>", "")
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("~~(.*?)~~"), "$1")
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^[-*•]\\s+\\[[ xX]\\]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^[-*•]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^>\\s+", RegexOption.MULTILINE), "")
            .trim()
    }

    /**
     * Parses raw formatted text into a rich AnnotatedString with bold, italic, underline, highlight, and strikethrough.
     */
    fun toAnnotatedString(raw: String): AnnotatedString {
        val cleanTokens = StringBuilder()
        data class StyleSpan(val start: Int, val end: Int, val style: SpanStyle)
        val spans = mutableListOf<StyleSpan>()

        var i = 0
        val len = raw.length

        while (i < len) {
            when {
                raw.startsWith("**", i) -> {
                    val endIdx = raw.indexOf("**", i + 2)
                    if (endIdx != -1) {
                        val inner = raw.substring(i + 2, endIdx)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(StyleSpan(start, cleanTokens.length, SpanStyle(fontWeight = FontWeight.Bold)))
                        i = endIdx + 2
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                raw.startsWith("<u>", i, ignoreCase = true) -> {
                    val endIdx = raw.indexOf("</u>", i + 3, ignoreCase = true)
                    if (endIdx != -1) {
                        val inner = raw.substring(i + 3, endIdx)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(StyleSpan(start, cleanTokens.length, SpanStyle(textDecoration = TextDecoration.Underline)))
                        i = endIdx + 4
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                raw.startsWith("<s>", i, ignoreCase = true) -> {
                    val endIdx = raw.indexOf("</s>", i + 3, ignoreCase = true)
                    if (endIdx != -1) {
                        val inner = raw.substring(i + 3, endIdx)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(StyleSpan(start, cleanTokens.length, SpanStyle(textDecoration = TextDecoration.LineThrough)))
                        i = endIdx + 4
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                raw.startsWith("~~", i) -> {
                    val endIdx = raw.indexOf("~~", i + 2)
                    if (endIdx != -1) {
                        val inner = raw.substring(i + 2, endIdx)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(StyleSpan(start, cleanTokens.length, SpanStyle(textDecoration = TextDecoration.LineThrough)))
                        i = endIdx + 2
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                raw.startsWith("<mark", i, ignoreCase = true) -> {
                    val tagEnd = raw.indexOf('>', i)
                    val closeTag = raw.indexOf("</mark>", i, ignoreCase = true)
                    if (tagEnd != -1 && closeTag != -1 && closeTag > tagEnd) {
                        val tagContent = raw.substring(i, tagEnd)
                        val colorMatch = Regex("color=[\"']?([^\"'>\\s]+)[\"']?", RegexOption.IGNORE_CASE).find(tagContent)
                        val color = if (colorMatch != null) hexToColor(colorMatch.groupValues[1]) else HighlightYellow
                        val inner = raw.substring(tagEnd + 1, closeTag)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(
                            StyleSpan(
                                start,
                                cleanTokens.length,
                                SpanStyle(background = color.copy(alpha = 0.45f), color = HighlightInk)
                            )
                        )
                        i = closeTag + 7
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                raw.startsWith("*", i) && !raw.startsWith("**", i) -> {
                    val endIdx = raw.indexOf("*", i + 1)
                    if (endIdx != -1 && endIdx > i + 1) {
                        val inner = raw.substring(i + 1, endIdx)
                        val start = cleanTokens.length
                        cleanTokens.append(inner)
                        spans.add(StyleSpan(start, cleanTokens.length, SpanStyle(fontStyle = FontStyle.Italic)))
                        i = endIdx + 1
                    } else {
                        cleanTokens.append(raw[i])
                        i++
                    }
                }
                else -> {
                    cleanTokens.append(raw[i])
                    i++
                }
            }
        }

        return buildAnnotatedString {
            append(cleanTokens.toString())
            for (span in spans) {
                if (span.start < span.end && span.start >= 0 && span.end <= cleanTokens.length) {
                    addStyle(span.style, span.start, span.end)
                }
            }
        }
    }
}
