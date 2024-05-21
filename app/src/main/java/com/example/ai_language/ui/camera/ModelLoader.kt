package com.example.ai_language.ui.camera

import android.content.Context
import android.util.Log
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
class ModelLoader(private val context: Context) {
    companion object {
        private const val MODEL_FILE = "best0519.torchscript.ptl"
        private const val CLASSES_FILE = "classes.txt"
    }

    fun loadModel(): Module {
        return LiteModuleLoader.load(assetFilePath(MODEL_FILE))
    }

    fun loadClassNames(): List<String> {
        val classNames = mutableListOf<String>()
        try {
            context.assets.open(CLASSES_FILE).bufferedReader().useLines { lines ->
                lines.forEach {
                    classNames.add(it)
                    Log.d("ModelLoader", "Loaded class name: $it")
                }
            }
        } catch (e: IOException) {
            Log.e("ModelLoader", "Error reading class names", e)
            return emptyList()
        }
        return classNames
    }

    private fun assetFilePath(assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (!file.exists()) {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
            }
        }
        return file.absolutePath
    }
}