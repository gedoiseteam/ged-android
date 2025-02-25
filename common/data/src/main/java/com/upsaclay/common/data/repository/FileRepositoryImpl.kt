package com.upsaclay.common.data.repository

import android.content.Context
import android.net.Uri
import com.upsaclay.common.domain.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

internal class FileRepositoryImpl(private val context: Context) : FileRepository {
    override suspend fun createFileFromUri(fileName: String, uri: Uri): File = withContext(Dispatchers.IO) {
        val extension = getFileType(uri) ?: "jpeg"
        val fileByteArray = getFileByteArray(uri)
        return@withContext createFileFromByteArray("$fileName.$extension", fileByteArray)
    }

    override suspend fun createFileFromByteArray(fileName: String, byteArray: ByteArray): File = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, fileName)
        val outPutStream = file.outputStream()
        outPutStream.write(byteArray)
        outPutStream.close()
        file
    }

    private fun getFileByteArray(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int

        inputStream?.let {
            while (inputStream.read(buffer).also { length = it } > 0) {
                byteArrayOutputStream.write(buffer, 0, length)
            }
            inputStream.close()
        }

        return byteArrayOutputStream.toByteArray()
    }

    override fun getFileType(uri: Uri): String? =
        context.contentResolver.getType(uri)?.split("/")?.last()
}
