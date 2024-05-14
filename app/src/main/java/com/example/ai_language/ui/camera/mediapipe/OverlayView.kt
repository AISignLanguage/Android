//package com.example.ai_language.ui.camera.mediapipe
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//import com.google.mediapipe.tasks.vision.core.RunningMode
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
//import kotlin.math.max
//import kotlin.math.min
//
//// 랜드마크 포인트 그리는 클래스
//class OverlayView(context: Context?, attrs: AttributeSet?) :
//    View(context, attrs) {
//
//    private var results: HandLandmarkerResult? = null   // 손의 랜드마크 결과 저장하는 객체
//    private var linePaint = Paint()     // 랜드마크를 연결하는 선의 스타일을 설정
//    private var pointPaint = Paint()    // 랜드마크를 그리는 데 사용되는 점의 스타일을 설정
//
//    // 이미지 크기 및 화면 크기와의 비율
//    private var scaleFactor: Float = 1f
//    private var imageWidth: Int = 1
//    private var imageHeight: Int = 1
//
//    init {
//        initPaints()
//    }
//
//    // OverlayView의 결과를 지우고 새로 고침 함수
//    fun clear() {
//        results = null
//        linePaint.reset()
//        pointPaint.reset()
//        invalidate()
//        initPaints()
//    }
//
//    // linePaint 및 pointPaint를 초기화 함수 (선, 점 스타일)
//    private fun initPaints() {
//        //linePaint.color =
//            //ContextCompat.getColor(context!!, R.color.mp_color_primary)
//        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
//        linePaint.style = Paint.Style.STROKE
//
//        pointPaint.color = Color.YELLOW
//        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
//        pointPaint.style = Paint.Style.FILL
//    }
//
//    // OverlayView의 그리기 로직을 정의하는 함수
//    override fun draw(canvas: Canvas) {
//        super.draw(canvas)
//        results?.let { handLandmarkerResult ->
//            for (landmark in handLandmarkerResult.landmarks()) {
//                for (normalizedLandmark in landmark) {
//                    canvas.drawPoint(
//                        normalizedLandmark.x() * imageWidth * scaleFactor,
//                        normalizedLandmark.y() * imageHeight * scaleFactor,
//                        pointPaint
//                    )
//                }
//
//                HandLandmarker.HAND_CONNECTIONS.forEach {
//                    canvas.drawLine(
//                        landmark.get(it!!.start())
//                            .x() * imageWidth * scaleFactor,
//                        landmark.get(it.start())
//                            .y() * imageHeight * scaleFactor,
//                        landmark.get(it.end())
//                            .x() * imageWidth * scaleFactor,
//                        landmark.get(it.end())
//                            .y() * imageHeight * scaleFactor,
//                        linePaint
//                    )
//                }
//            }
//        }
//    }
//
//    // 새로운 랜드마크 결과를 설정하고, 화면에 출력할 이미지의 크기와 런타임 모드를 설정하는 함수
//    fun setResults(
//        handLandmarkerResults: HandLandmarkerResult,
//        imageHeight: Int,
//        imageWidth: Int,
//        runningMode: RunningMode = RunningMode.IMAGE
//    ) {
//        results = handLandmarkerResults
//
//        this.imageHeight = imageHeight
//        this.imageWidth = imageWidth
//
//        scaleFactor = when (runningMode) {
//            RunningMode.IMAGE,
//            RunningMode.VIDEO -> {
//                min(width * 1f / imageWidth, height * 1f / imageHeight)
//            }
//            RunningMode.LIVE_STREAM -> {
//                // PreviewView is in FILL_START mode. So we need to scale up the
//                // landmarks to match with the size that the captured images will be
//                // displayed.
//                max(width * 1f / imageWidth, height * 1f / imageHeight)
//            }
//        }
//        invalidate()
//    }
//
//    companion object {
//        private const val LANDMARK_STROKE_WIDTH = 8F    // 랜드마크를 그릴 때 사용되는 선의 두께
//    }
//}