package com.example.ai_language.ui.camera

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream

class ImageAnalyzer(
    private val model: Module,
    private val classNames: List<String>,
    private val detectionInfo: TextView,
    private val context: Context
) : ImageAnalysis.Analyzer {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmapWithCorrectRotation()
        if (bitmap == null) {
            imageProxy.close()
            return
        }

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        val outputIValue = model.forward(IValue.from(inputTensor))
        val outputTensor = if (outputIValue.isTuple) {
            outputIValue.toTuple()[0].toTensor()
        } else {
            outputIValue.toTensor()
        }

        val outputArray = outputTensor.dataAsFloatArray
        val (boundingBoxes, maxProb, bestClassIndex) = extractBoundingBoxes(outputArray, 0.25f, outputTensor.shape())

        val annotatedBitmap = drawBoundingBoxes(resizedBitmap, boundingBoxes)

        (context as Activity).runOnUiThread {
            var className = if (bestClassIndex in classNames.indices) {
                classNames[bestClassIndex]
            } else {
                ""
            }
            when(className) {
            "best" -> className = "최고야"
            "call" -> className = "전화"
            "fine" -> className = "괜찮아"
            "happy" -> className = "행복해"
            "heart" -> className = "하트"
            "hello" -> className = "안녕"
            "iloveyou" -> className = "사랑해"
            "me" -> className = "나"
            "meet" -> className = "만나다"
            "peace" -> className = "평화"
            "see" -> className = "보다"
            "smile" -> className = "웃다"
            "what" -> className = "무엇"
            }

            detectionInfo.text = "$className"
            //detectionInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, BitmapDrawable(context.resources, annotatedBitmap))
            Log.d("ImageAnalyzer", "Class: $className - Score: $maxProb")
        }

        imageProxy.close()
    }

    private fun extractBoundingBoxes(outputArray: FloatArray, threshold: Float, outputShape: LongArray): Triple<List<RectF>, Float, Int> {
        val boundingBoxes = mutableListOf<RectF>()
        var maxProbability = 0.0f
        var bestClassIndex = -1

        val numBoxes = outputShape[1].toInt()
        val numClasses = outputShape[2].toInt() - 5

        for (i in 0 until numBoxes) {
            val offset = i * (numClasses + 5)
            val x = outputArray[offset + 0]
            val y = outputArray[offset + 1]
            val w = outputArray[offset + 2]
            val h = outputArray[offset + 3]
            val objectness = outputArray[offset + 4]

            val classProbabilities = outputArray.sliceArray(offset + 5 until offset + 5 + numClasses)
            val (classIndex, classProbability) = classProbabilities.withIndex().maxByOrNull { it.value } ?: continue

            val score = objectness * classProbability
            if (score > threshold) {
                boundingBoxes.add(RectF(x - w / 2, y - h / 2, x + w / 2, y + h / 2))
                if (score > maxProbability) {
                    maxProbability = score
                    bestClassIndex = classIndex
                }
            }
        }
        return Triple(boundingBoxes, maxProbability, bestClassIndex)
    }

    private fun drawBoundingBoxes(bitmap: Bitmap, boundingBoxes: List<RectF>): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        boundingBoxes.forEach { box ->
            canvas.drawRect(box, paint)
        }

        return resultBitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ImageProxy.toBitmapWithCorrectRotation(): Bitmap? {
        val bitmap = this.toBitmap() ?: return null
        val rotationDegrees = this.imageInfo.rotationDegrees
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ImageProxy.toBitmap(): Bitmap? {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}