package com.example.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.data.local.JlabDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {
    private const val TAG = "BackupManager"

    suspend fun createBackup(context: Context, destinationUri: Uri? = null): File? =
        withContext(Dispatchers.IO) {
            try {
                // Ensure room db commits all wal entries
                val db = JlabDatabase.getInstance(context)
                val cursor = db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL);")
                cursor.moveToFirst()
                cursor.close()

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val backupFileName = "Jlab_Backup_$timeStamp.zip"
                val tempZipFile = File(context.cacheDir, backupFileName)

                val dbFile = context.getDatabasePath("jlab.db")
                val vaultDir = File(context.filesDir, "vault")

                ZipOutputStream(BufferedOutputStream(FileOutputStream(tempZipFile))).use { zos ->
                    // 1. Write metadata.json
                    val metadataJson = JSONObject().apply {
                        put("version", 1)
                        put("appName", "Jlab")
                        put("createdAt", System.currentTimeMillis())
                        put("createdAtFormatted", timeStamp)
                        put("databaseName", "jlab.db")
                    }
                    val metaBytes = metadataJson.toString(2).toByteArray(Charsets.UTF_8)
                    zos.putNextEntry(ZipEntry("metadata.json"))
                    zos.write(metaBytes)
                    zos.closeEntry()

                    // 2. Write Database
                    if (dbFile.exists()) {
                        zos.putNextEntry(ZipEntry("database/jlab.db"))
                        FileInputStream(dbFile).use { it.copyTo(zos) }
                        zos.closeEntry()
                    }

                    // 3. Write Vault files
                    if (vaultDir.exists() && vaultDir.isDirectory) {
                        vaultDir.listFiles()?.forEach { file ->
                            if (file.isFile) {
                                zos.putNextEntry(ZipEntry("vault/${file.name}"))
                                FileInputStream(file).use { it.copyTo(zos) }
                                zos.closeEntry()
                            }
                        }
                    }
                }

                // If user provided a SAF Uri, copy to that Uri
                if (destinationUri != null) {
                    context.contentResolver.openOutputStream(destinationUri)?.use { out ->
                        FileInputStream(tempZipFile).use { it.copyTo(out) }
                    }
                }

                tempZipFile
            } catch (e: Exception) {
                Log.e(TAG, "Backup creation failed: ${e.message}", e)
                null
            }
        }

    suspend fun restoreBackup(context: Context, sourceUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val tempExtractDir = File(context.cacheDir, "restore_temp_${System.currentTimeMillis()}")
                tempExtractDir.mkdirs()

                // Extract Zip
                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            val newFile = File(tempExtractDir, entry.name)
                            if (entry.isDirectory) {
                                newFile.mkdirs()
                            } else {
                                newFile.parentFile?.mkdirs()
                                FileOutputStream(newFile).use { fos ->
                                    zis.copyTo(fos)
                                }
                            }
                            zis.closeEntry()
                            entry = zis.nextEntry
                        }
                    }
                }

                // Verify valid package
                val metaFile = File(tempExtractDir, "metadata.json")
                val restoredDb = File(tempExtractDir, "database/jlab.db")
                if (!metaFile.exists() || !restoredDb.exists()) {
                    tempExtractDir.deleteRecursively()
                    return@withContext false
                }

                // Replace database file
                val targetDb = context.getDatabasePath("jlab.db")
                targetDb.parentFile?.mkdirs()

                // Close active instance connection before copy
                val activeDb = JlabDatabase.getInstance(context)
                if (activeDb.isOpen) {
                    activeDb.close()
                }

                // Also delete old wal and shm files
                File(targetDb.parentFile, "jlab.db-wal").delete()
                File(targetDb.parentFile, "jlab.db-shm").delete()

                restoredDb.copyTo(targetDb, overwrite = true)

                // Restore vault files
                val restoredVault = File(tempExtractDir, "vault")
                val targetVault = File(context.filesDir, "vault")
                targetVault.mkdirs()
                if (restoredVault.exists()) {
                    restoredVault.listFiles()?.forEach { file ->
                        file.copyTo(File(targetVault, file.name), overwrite = true)
                    }
                }

                tempExtractDir.deleteRecursively()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Restore failed: ${e.message}", e)
                false
            }
        }

    fun exportToMarkdown(title: String, content: String, metadata: Map<String, String>): String {
        val sb = StringBuilder()
        sb.append("# ").append(title).append("\n\n")
        metadata.forEach { (key, value) ->
            sb.append("**").append(key).append("**: ").append(value).append("\n")
        }
        sb.append("\n---\n\n")
        sb.append(content).append("\n")
        return sb.toString()
    }
}
