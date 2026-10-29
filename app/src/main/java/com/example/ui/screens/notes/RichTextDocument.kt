package com.example.ui.screens.notes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkExtendedColors
import com.example.ui.theme.HighlightBlue
import com.example.ui.theme.HighlightGreen
import com.example.ui.theme.HighlightInk
import com.example.ui.theme.HighlightPink
import com.example.ui.theme.HighlightPurple
import com.example.ui.theme.HighlightYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LightExtendedColors
import com.example.ui.theme.PlusJakartaSansFontFamily
import org.json.JSONArray
import org.json.JSONObject

data class DocumentSpan(
    val start: Int,
    val end: Int,
    val type: String,
    val value: String? = null
) {
    companion object {
        const val TYPE_BOLD = "BOLD"
        const val TYPE_ITALIC = "ITALIC"
        const val TYPE_UNDERLINE = "UNDERLINE"
        const val TYPE_STRIKETHROUGH = "STRIKETHROUGH"
        const val TYPE_HIGHLIGHT = "HIGHLIGHT"
        const val TYPE_HEADING_1 = "HEADING_1"
        const val TYPE_HEADING_2 = "HEADING_2"
        const val TYPE_HEADING_3 = "HEADING_3"
        const val TYPE_QUOTE = "QUOTE"
        const val TYPE_CODE = "CODE"
    }
}

object RichTextDocument {

    val SwatchYellow: Color = HighlightYellow
    val SwatchGreen: Color = HighlightGreen
    val SwatchBlue: Color = HighlightBlue
    val SwatchPink: Color = HighlightPink
    val SwatchPurple: Color = HighlightPurple

    fun hexToColor(hex: String?): Color {
        if (hex.isNullOrBlank()) return HighlightYellow
        return try {
            val clean = hex.removePrefix("#")
            val colorLong = clean.toLong(16)
            if (clean.length == 6) {
                Color(colorLong or 0xFF000000L)
            } else {
                Color(colorLong)
            }
        } catch (_: Exception) {
            HighlightYellow
        }
    }

    fun spansToJson(spans: List<DocumentSpan>): String {
        val array = JSONArray()
        for (span in spans) {
            if (span.end > span.start && span.start >= 0) {
                val obj = JSONObject()
                obj.put("start", span.start)
                obj.put("end", span.end)
                obj.put("type", span.type)
                if (span.value != null) {
                    obj.put("val", span.value)
                }
                array.put(obj)
            }
        }
        return array.toString()
    }

    fun jsonToSpans(json: String?): List<DocumentSpan> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<DocumentSpan>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val start = obj.getInt("start")
                val end = obj.getInt("end")
                val type = obj.getString("type")
                val value = if (obj.has("val")) obj.getString("val") else null
                if (end > start && start >= 0) {
                    list.add(DocumentSpan(start, end, type, value))
                }
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Converts any legacy markdown or HTML tags in existing database records into clean plain text + structured spans.
     */
    fun parseLegacyContentToDocument(rawContent: String, formattingJson: String?): Pair<String, List<DocumentSpan>> {
        val existingSpans = jsonToSpans(formattingJson)
        if (existingSpans.isNotEmpty()) {
            return Pair(rawContent, existingSpans)
        }

        val hasMarkup = rawContent.contains("**") ||
            rawContent.contains("<u>") ||
            rawContent.contains("<mark") ||
            rawContent.contains("~~") ||
            rawContent.contains("<s>") ||
            rawContent.lines().any { it.startsWith("#") || it.startsWith("> ") }

        if (!hasMarkup) {
            return Pair(rawContent, emptyList())
        }

        val plainBuilder = StringBuilder()
        val detectedSpans = mutableListOf<DocumentSpan>()
        var i = 0
        val len = rawContent.length

        while (i < len) {
            when {
                rawContent.startsWith("**", i) -> {
                    val endIdx = rawContent.indexOf("**", i + 2)
                    if (endIdx != -1) {
                        val inner = rawContent.substring(i + 2, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_BOLD))
                        i = endIdx + 2
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                rawContent.startsWith("<u>", i, ignoreCase = true) -> {
                    val endIdx = rawContent.indexOf("</u>", i + 3, ignoreCase = true)
                    if (endIdx != -1) {
                        val inner = rawContent.substring(i + 3, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_UNDERLINE))
                        i = endIdx + 4
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                rawContent.startsWith("<s>", i, ignoreCase = true) -> {
                    val endIdx = rawContent.indexOf("</s>", i + 3, ignoreCase = true)
                    if (endIdx != -1) {
                        val inner = rawContent.substring(i + 3, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_STRIKETHROUGH))
                        i = endIdx + 4
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                rawContent.startsWith("~~", i) -> {
                    val endIdx = rawContent.indexOf("~~", i + 2)
                    if (endIdx != -1) {
                        val inner = rawContent.substring(i + 2, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_STRIKETHROUGH))
                        i = endIdx + 2
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                rawContent.startsWith("<mark", i, ignoreCase = true) -> {
                    val closeTagIdx = rawContent.indexOf(">", i)
                    val endIdx = rawContent.indexOf("</mark>", i, ignoreCase = true)
                    if (closeTagIdx != -1 && endIdx != -1 && endIdx > closeTagIdx) {
                        val tagHeader = rawContent.substring(i, closeTagIdx + 1)
                        val colorMatch = Regex("color=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(tagHeader)
                        val colorHex = colorMatch?.groupValues?.get(1) ?: "#FFF176"
                        val inner = rawContent.substring(closeTagIdx + 1, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_HIGHLIGHT, colorHex))
                        i = endIdx + 7
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                rawContent.startsWith("*", i) && !rawContent.startsWith("**", i) -> {
                    val endIdx = rawContent.indexOf("*", i + 1)
                    if (endIdx != -1 && !rawContent.startsWith("**", endIdx)) {
                        val inner = rawContent.substring(i + 1, endIdx)
                        val start = plainBuilder.length
                        plainBuilder.append(inner)
                        detectedSpans.add(DocumentSpan(start, plainBuilder.length, DocumentSpan.TYPE_ITALIC))
                        i = endIdx + 1
                    } else {
                        plainBuilder.append(rawContent[i])
                        i++
                    }
                }
                else -> {
                    plainBuilder.append(rawContent[i])
                    i++
                }
            }
        }

        val plainText = plainBuilder.toString()
        val finalSpans = detectedSpans.toMutableList()
        var currentOffset = 0
        val lines = plainText.split("\n")
        val cleanedLines = mutableListOf<String>()

        for (line in lines) {
            when {
                line.startsWith("### ") -> {
                    val cleanLine = line.removePrefix("### ")
                    cleanedLines.add(cleanLine)
                    finalSpans.add(DocumentSpan(currentOffset, currentOffset + cleanLine.length, DocumentSpan.TYPE_HEADING_3))
                    currentOffset += cleanLine.length + 1
                }
                line.startsWith("## ") -> {
                    val cleanLine = line.removePrefix("## ")
                    cleanedLines.add(cleanLine)
                    finalSpans.add(DocumentSpan(currentOffset, currentOffset + cleanLine.length, DocumentSpan.TYPE_HEADING_2))
                    currentOffset += cleanLine.length + 1
                }
                line.startsWith("# ") -> {
                    val cleanLine = line.removePrefix("# ")
                    cleanedLines.add(cleanLine)
                    finalSpans.add(DocumentSpan(currentOffset, currentOffset + cleanLine.length, DocumentSpan.TYPE_HEADING_1))
                    currentOffset += cleanLine.length + 1
                }
                line.startsWith("> ") -> {
                    val cleanLine = line.removePrefix("> ")
                    cleanedLines.add(cleanLine)
                    finalSpans.add(DocumentSpan(currentOffset, currentOffset + cleanLine.length, DocumentSpan.TYPE_QUOTE))
                    currentOffset += cleanLine.length + 1
                }
                else -> {
                    cleanedLines.add(line)
                    currentOffset += line.length + 1
                }
            }
        }

        val finalText = cleanedLines.joinToString("\n")
        return Pair(finalText, finalSpans)
    }

    fun toggleSpan(
        spans: List<DocumentSpan>,
        start: Int,
        end: Int,
        type: String,
        value: String? = null
    ): List<DocumentSpan> {
        if (start >= end) return spans

        val result = mutableListOf<DocumentSpan>()
        var rangeAlreadyCovered = false

        for (s in spans) {
            if (s.type == type && (value == null || s.value == value)) {
                if (s.start <= start && s.end >= end) {
                    rangeAlreadyCovered = true
                    if (s.start < start) {
                        result.add(DocumentSpan(s.start, start, type, s.value))
                    }
                    if (s.end > end) {
                        result.add(DocumentSpan(end, s.end, type, s.value))
                    }
                } else {
                    result.add(s)
                }
            } else {
                result.add(s)
            }
        }

        if (!rangeAlreadyCovered) {
            result.add(DocumentSpan(start, end, type, value))
        }

        return mergeContiguousSpans(result)
    }

    fun mergeContiguousSpans(spans: List<DocumentSpan>): List<DocumentSpan> {
        val byTypeAndVal = spans.groupBy { "${it.type}_${it.value ?: ""}" }
        val mergedList = mutableListOf<DocumentSpan>()

        for ((_, group) in byTypeAndVal) {
            val sorted = group.sortedBy { it.start }
            var current: DocumentSpan? = null

            for (span in sorted) {
                if (current == null) {
                    current = span
                } else if (span.start <= current.end) {
                    current = DocumentSpan(
                        start = current.start,
                        end = maxOf(current.end, span.end),
                        type = current.type,
                        value = current.value
                    )
                } else {
                    mergedList.add(current)
                    current = span
                }
            }
            if (current != null) {
                mergedList.add(current)
            }
        }

        return mergedList.sortedBy { it.start }
    }

    fun isTypeActive(spans: List<DocumentSpan>, start: Int, end: Int, type: String): Boolean {
        if (start >= end) return false
        return spans.any { it.type == type && it.start <= start && it.end >= end }
    }

    private fun spanStyleFor(span: DocumentSpan, isDarkTheme: Boolean): SpanStyle? {
        val extended = if (isDarkTheme) DarkExtendedColors else LightExtendedColors
        return when (span.type) {
            DocumentSpan.TYPE_BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
            DocumentSpan.TYPE_ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
            DocumentSpan.TYPE_UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
            DocumentSpan.TYPE_STRIKETHROUGH -> SpanStyle(textDecoration = TextDecoration.LineThrough)
            DocumentSpan.TYPE_HIGHLIGHT -> {
                val bg = hexToColor(span.value)
                SpanStyle(
                    background = bg.copy(alpha = 0.85f),
                    color = HighlightInk
                )
            }
            DocumentSpan.TYPE_HEADING_1 -> SpanStyle(
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            DocumentSpan.TYPE_HEADING_2 -> SpanStyle(
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            DocumentSpan.TYPE_HEADING_3 -> SpanStyle(
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            DocumentSpan.TYPE_QUOTE -> SpanStyle(
                fontStyle = FontStyle.Italic,
                color = extended.quoteText
            )
            DocumentSpan.TYPE_CODE -> SpanStyle(
                fontFamily = JetBrainsMonoFontFamily,
                background = extended.codeSurface
            )
            else -> null
        }
    }

    class DocumentVisualTransformation(
        private val spans: List<DocumentSpan>,
        private val isDarkTheme: Boolean
    ) : VisualTransformation {

        override fun filter(text: AnnotatedString): TransformedText {
            val raw = text.text
            val annotated = documentToAnnotatedString(raw, spans, isDarkTheme)
            return TransformedText(annotated, OffsetMapping.Identity)
        }
    }

    fun documentToAnnotatedString(
        plainText: String,
        spans: List<DocumentSpan>,
        isDarkTheme: Boolean
    ): AnnotatedString {
        val len = plainText.length
        return buildAnnotatedString {
            append(plainText)
            for (span in spans) {
                val s = span.start.coerceIn(0, len)
                val e = span.end.coerceIn(0, len)
                if (e > s) {
                    spanStyleFor(span, isDarkTheme)?.let { style ->
                        addStyle(style, s, e)
                    }
                }
            }
        }
    }
}
