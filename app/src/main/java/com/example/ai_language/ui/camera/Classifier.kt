package com.example.ai_language.ui.camera

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.flex.FlexDelegate
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

// 모델과 관련된 작업 수행하는 클래스
class Classifier(context: Context) {

    var context: Context = context
    private lateinit var interpreter: Interpreter
    private lateinit var inputImage: TensorImage

    // 입력 이미지 전처리 - 1. 모델의 입력 크기 확인
    var modelInputWidth: Int = 0
    var modelInputHeight: Int = 0
    var modelInputChanner: Int = 0

    // 추론 및 결과 해석 - 추론
    lateinit var outputBuffer: TensorBuffer // 추론: 모델의 추론된 출력 값을 저장할 프로퍼티 선언
    var modelOutputClasses: Int = 0

    companion object {
        const val MODEL_NAME = "model.tflite"
    }

    init {
        // 모델 초기화
        val model: ByteBuffer? = loadModelFile(MODEL_NAME)
        model?.order(ByteOrder.nativeOrder())?:throw IOException()

        //val options = Interpreter.Options()
        //val flexDelegate = FlexDelegate()
        //options.addDelegate(flexDelegate)
        //interpreter = Interpreter(model, options)

        // Interpreter 생성 -> 모델에 데이터를 입력하고 추론 결과를 전달받을 수 있는 클래스
        interpreter = Interpreter(model)

        initModelShape() // 모델의 입출력 크기 계산 메소드
    }

    // assets 폴더에서 tflite 파일을 읽어오는 함수
    // tflite 파일명을 입력받아 ByteBuffer 클래스로 모델을 반환
    private fun loadModelFile(modelName: String): ByteBuffer? {

        val assetManger = this.context.assets
        val afd: AssetFileDescriptor? = assetManger.openFd(modelName)
        if (afd == null) {
            throw IOException() // 자신을 호출한 쪽에서 예외처리 요구
            return null
        }

        val fileInputStream = FileInputStream(afd.fileDescriptor)
        val fileChannel = fileInputStream.channel

        // 파일디스크립터 오프셋과 길이
        val startOffset = afd.startOffset
        val declaredLength = afd.declaredLength

        // FileChannel.map() 메서드로 ByteBuffer 클래스를 상속한 MappedByteBuffer 인스턴스 생성
        // 파라미터: 참조모드, 오프셋, 길이
        // 최종적으로 tflite 파일을 ByteBuffer 형으로 읽어오는데 성공!
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    ////// 수정 필요 ////// ////// 수정 필요 //////
    // 모델의 입출력 클래스 수(크기) 계산(확인) 함수
    // 모델의 입력 텐서를 가져와 형태를 확인하고, 입력 이미지의 너비, 높이 및 채널 수를 저장
    private fun initModelShape() {
        // getInputTensor()로 입력 텐서 가져오기
        var inputTensor: Tensor = interpreter.getInputTensor(0)

        // shape()로 입력 텐서 형상 가져오고 프로퍼티에 저장
        val inputShape = inputTensor.shape()
        modelInputChanner = inputShape[0]
        modelInputWidth = inputShape[1]
        modelInputHeight = inputShape[2]

        inputImage = TensorImage(inputTensor.dataType())

        // 추론 -> 모델의 출력값 저장할 TensorBuffer 생성
        val outputTensor = interpreter.getOutputTensor(0)
        val outputShape = outputTensor.shape()
        modelOutputClasses = outputShape[1]
        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType())
    }

    ////// 수정 필요 ////// ////// 수정 필요 //////
    // 모델 결과 추론 함수
    // 입력 이미지를 모델에 전달하기 전에 전처리를 수행하고, 모델을 실행하여 결과를 반환
    // 출력 클래스 수 이용하여 출력값 담을 배열 생성 후 interpreter의 run() 메서드에 전달하여 추론 수행
//     fun classify(image: Bitmap): Pair<String, Float> {
//        // 전처리된 입력 이미지
//        val buffer = ImageProxy.t (resizeBitmap(image))
//        var result = FloatArray(modelOutputClasses) ///추론 결과를 담을 배열
//        interpreter.run(buffer, result) //추론 수행
//
//        // 확률이 가장 높은 클래스와 확률 반환
//        return argmax(result)
//    }

    ////// 수정 필요 //////  ////// 수정 필요 //////
    // 추론 결과 해석 함수 -> 추론 결과값 확인하여 확률 가장 높은 클래스 반환
//    private fun argmax(array: FloatArray): Pair<String, Float> {
//        var argmaxIndex: Int = 0
//        var maxProbability: Float = array[0]
//
//        for (i in 1 until array.size) {
//            val probability = array[i]
//            if (probability > maxProbability) {
//                argmaxIndex = i
//                maxProbability = probability
//            }
//        }
//        //val className = classes[argmaxIndex]
//        return Pair(className, maxProbability)
//    }

    // 입력 이미지 크기 변환 함수 (이미지 크기 조절)
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        // 파라미터: 비트맵 인스턴스, 새로운 너비, 새로운 높이, 이미지 보간법
        // 이미지 늘릴 때 -> true로 설정, 양선형보간법 (이미지 품질) , 줄일 때 -> false로 설정, 최근접 보간법 (성능)
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false)
    }

    // 데이터 전처리
    // CameraX에서 제공되는 이미지를 일반적인 비트맵 형식으로 변환하여
    // 이미지 처리나 모델 추론 등의 작업을 수행하는 함수
    fun ImageProxy.toBitmap(): Bitmap? {
        // 플레인 및 버퍼 가져오기
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        // 버퍼 크기 계산
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        // U and V are swapped (NV21 형식으로 이미지 데이터 병합)
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // YUVImage 생성 및 JPEG 압축
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // 입력 이미지 채널과 포맷 변환 함수
    // 입력 이미지를 정규화하고 ByteBuffer에 추가
    private fun convertBitmapToGrayByteBuffer(bitmap: Bitmap): ByteBuffer {
        // 바이트 크기만큼 ByteBuffer 메모리를 할당
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        // 모델과 동일한 바이트 순서로 설정
        byteBuffer.order(ByteOrder.nativeOrder())

        // 수정 필요
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.forEach { pixel ->
            val r = pixel shr 16 and 0xFF
            val g = pixel shr 8 and 0xFF
            val b = pixel and 0xFF

            val avgPixelValue = (r + g + b) / 3.0f
            val normalizedPixelValue = avgPixelValue / 255.0f

            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }

    // interpreter 자원 해제 함수
    public fun finish() {
        if(interpreter != null) interpreter.close()
    }

}