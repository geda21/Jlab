package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.util.UUID

object FileStorageHelper {

    fun copyUriToInternalStorage(context: Context, uri: Uri, subDirectory: String = "attachments"): File? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = getFileNameFromUri(context, uri) ?: "file_${System.currentTimeMillis()}"
            // Sanitize file name
            fileName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")

            val targetDir = File(context.filesDir, subDirectory)
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val targetFile = File(targetDir, "${UUID.randomUUID().toString().take(8)}_$fileName")
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val outputStream = FileOutputStream(targetFile)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                targetFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        fileName = it.getString(index)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) path.substring(cut + 1) else path
            }
        }
        return fileName
    }

    fun getFormattedFileSize(sizeBytes: Long): String {
        if (sizeBytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceAtMost(units.size - 1)
        return DecimalFormat("#,##0.#").format(sizeBytes / Math.pow(1024.0, index.toDouble())) + " " + units[index]
    }

    fun getDocumentTypeIcon(fileName: String, mimeType: String): ImageVector {
        val lowerName = fileName.lowercase()
        val lowerMime = mimeType.lowercase()
        return when {
            lowerName.endsWith(".pdf") || lowerMime.contains("pdf") -> Icons.Default.PictureAsPdf
            lowerName.endsWith(".doc") || lowerName.endsWith(".docx") || lowerMime.contains("word") -> Icons.Default.Description
            lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") || lowerMime.contains("sheet") || lowerMime.contains("excel") -> Icons.Default.TableChart
            lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx") || lowerMime.contains("presentation") || lowerMime.contains("powerpoint") -> Icons.Default.Slideshow
            lowerName.endsWith(".txt") || lowerName.endsWith(".md") || lowerMime.contains("text") -> Icons.Default.Article
            lowerName.endsWith(".zip") || lowerName.endsWith(".rar") || lowerName.endsWith(".7z") || lowerMime.contains("zip") -> Icons.Default.FolderZip
            lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".webp") || lowerMime.contains("image") -> Icons.Default.Image
            else -> Icons.Default.InsertDriveFile
        }
    }

    fun getDocumentTypeColor(fileName: String, mimeType: String): Color {
        val lowerName = fileName.lowercase()
        val lowerMime = mimeType.lowercase()
        return when {
            lowerName.endsWith(".pdf") || lowerMime.contains("pdf") -> com.example.ui.theme.JlabRuby
            lowerName.endsWith(".doc") || lowerName.endsWith(".docx") || lowerMime.contains("word") -> com.example.ui.theme.JlabCyanDarkText
            lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") || lowerMime.contains("sheet") || lowerMime.contains("excel") -> com.example.ui.theme.JlabEmerald
            lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx") || lowerMime.contains("presentation") || lowerMime.contains("powerpoint") -> com.example.ui.theme.JlabAmber
            lowerName.endsWith(".txt") || lowerName.endsWith(".md") || lowerMime.contains("text") -> com.example.ui.theme.JlabViolet
            lowerName.endsWith(".zip") || lowerName.endsWith(".rar") || lowerMime.contains("zip") -> com.example.ui.theme.JlabSlate
            lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerMime.contains("image") -> com.example.ui.theme.JlabCyan
            else -> com.example.ui.theme.JlabIndigoPrimary
        }
    }
}
