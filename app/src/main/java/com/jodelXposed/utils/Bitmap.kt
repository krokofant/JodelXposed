package com.jodelXposed.utils

import android.graphics.BitmapFactory
import android.os.Environment
import com.jodelXposed.utils.Log.xlog
import java.io.*

object Bitmap {
    @JvmField var jodelImagePath = Environment.getExternalStorageDirectory().toString() + "/.jodel-input.jpg"

    @JvmStatic
    fun loadBitmap(): android.graphics.Bitmap {
        xlog("Loading bitmap image")
        val file = File(jodelImagePath)
        return if (file.exists()) bitmapFromFile(file) else bitmapFromFile(File(jodelImagePath))
    }

    @JvmStatic
    fun saveBitmap(bitmap: android.graphics.Bitmap) {
        try {
            xlog("Saving bitmap of size: " + bitmap.byteCount)
            val fos = FileOutputStream(File(jodelImagePath))
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            xlog("File not found: " + e.message)
        } catch (e: IOException) {
            xlog("Error accessing file: " + e.message)
        }

    }

    @JvmStatic
    fun bitmapFromFile(file: File): android.graphics.Bitmap {
        val blob = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if (bitmap != null)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, blob)
        else
            xlog("Loaded bitmap is null")
        return bitmap
    }
}