package com.chihihx.launcher.utils

import android.content.Context
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * Created by ZTMIDGO on 2018/5/1.
 */
object FileUtils {
    @Throws(IOException::class)
    fun copyAssets(assetManager: AssetManager, path: String, outPath: File) {
        val assets = assetManager.list(path)

        if (assets != null) {
            if (assets.size == 0) {
                copyFile(assetManager, path, outPath)
            } else {
                val dir = File(outPath, path)
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                    }
                }

                val var5: Array<String> = assets
                val var6 = assets.size

                for (var7 in 0 until var6) {
                    val asset = var5[var7]
                    copyAssets(assetManager, "$path/$asset", outPath)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(assetManager: AssetManager, fileName: String, outPath: File) {
        val `in` = assetManager.open(fileName)
        val out: OutputStream = FileOutputStream("$outPath/$fileName")
        val buffer = ByteArray(4000)

        var read: Int
        while ((`in`.read(buffer).also { read = it }) != -1) {
            out.write(buffer, 0, read)
        }

        `in`.close()
        out.close()
    }

    @Throws(IOException::class)
    fun write(`in`: InputStream, filePath: String, name: String) {
        createPath(File(filePath))
        var index: Int
        val bytes = ByteArray(1024)
        val out: OutputStream = FileOutputStream("$filePath/$name")
        while ((`in`.read(bytes).also { index = it }) != -1) {
            out.write(bytes, 0, index)
            out.flush()
        }
        `in`.close()
        out.close()
    }

    fun createPath(file: File) {
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun getFileSuffix(file: File): String? {
        return getFileSuffix(file.name)
    }

    fun getFileSuffix(fileName: String): String? {
        val index = fileName.lastIndexOf(".")
        return if (index == -1) null else fileName.substring(index)
    }

    fun getFileNameWithoutSuffix(fileName: String): String {
        return fileName.substring(0, fileName.lastIndexOf("."))
    }

    fun getBitmapBytes(bitmap: Bitmap, format: CompressFormat?, quality: Int): ByteArray? {
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(format!!, quality, baos)
            val data = baos.toByteArray()
            baos.close()
            return data
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteFile(filePath: String?) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    fun deleteAllFile(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }

        val files = file.listFiles() ?: return

        for (i in files.indices) {
            val f = files[i]
            if (f.isFile) {
                f.delete()
            } else {
                deleteAllFile(f)
                f.delete()
            }
        }

        file.delete()
    }

    fun renameFile(filePath: String?, newName: String): File {
        val oldFile = File(filePath)
        val basePath = oldFile.parent
        val newFile = File("$basePath/$newName")
        oldFile.renameTo(newFile)
        return newFile
    }

    @Throws(IOException::class)
    fun writeFile(data: ByteArray?, filePath: String, fileName: String): File {
        createPath(File(filePath))
        val file = File("$filePath/$fileName")
        val fc = FileOutputStream(file).channel
        fc.write(ByteBuffer.wrap(data))
        fc.close()
        return file
    }

    @Throws(IOException::class)
    fun readStream(response: InputStream): ByteArray? {
        val pool = ByteArray(1024)
        val out = ByteArrayOutputStream()
        var bytesRead = 0
        try {
            while ((response.read(pool).also { bytesRead = it }) > 0) {
                out.write(pool, 0, bytesRead)
            }
            return out.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            out.close()
            response.close()
        }
        return null
    }

    fun readChannel(filePath: String?): ByteArray? {
        try {
            val fc = FileInputStream(filePath).channel
            val data = ByteArray(fc.size().toInt())
            fc.read(ByteBuffer.wrap(data))
            fc.close()
            return data
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun readAssets(context: Context, name: String?): ByteArray? {
        try {
            val `in` = context.assets.open(name!!)
            val buffer = ByteArray(`in`.available())
            `in`.read(buffer)
            `in`.close()
            return buffer
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun writeFile(`in`: InputStream, filePath: String?) {
        val pool = ByteArray(1024)
        val out = FileOutputStream(File(filePath))
        var bytesRead = 0
        try {
            while ((`in`.read(pool).also { bytesRead = it }) > 0) {
                out.write(pool, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            out.close()
            `in`.close()
        }
    }

    fun parseRealPathFromUri(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}
