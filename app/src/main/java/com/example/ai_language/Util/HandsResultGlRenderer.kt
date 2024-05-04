package com.example.ai_language.Util

import android.opengl.GLES20
import android.util.Log
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsResult
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HandsResultGlRenderer: ResultGlRenderer<HandsResult?> {

    private var program = 0
    private var positionHandle = 0
    private var projectionMatrixHandle = 0
    private var colorHandle = 0

    //셰이더 코드를 컴파일하고 셰이더 ID를 반환 함수
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    //OpenGL ES 프로그램을 초기화하고 속성/유니폼 핸들을 검색 함수
    override fun setupRendering() {
        program = GLES20.glCreateProgram()
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix")
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
    }

    // 랜드마크 간의 연결 및 랜드마크 자체를 포함한 손 추적 결과를 렌더링 함수
    override fun renderResult(result: HandsResult?, projectionMatrix: FloatArray?) {
        if (result == null) {
            return
        }
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
        GLES20.glLineWidth(CONNECTION_THICKNESS)
        val numHands = result.multiHandLandmarks().size
        for (i in 0 until numHands) {
            val isLeftHand = result.multiHandedness()[i].label == "Left"
            drawConnections(
                result.multiHandLandmarks()[i].landmarkList,
                if (isLeftHand) LEFT_HAND_CONNECTION_COLOR else RIGHT_HAND_CONNECTION_COLOR
            )
            for(ind in result.multiHandLandmarks()[i].landmarkList.indices) {
                val lm = result.multiHandLandmarks()[i].landmarkList[ind]
                Log.d(TAG, "LandMark[$ind] | x : ${lm.x}, y : ${lm.y}, z : ${lm.z}")
            }
            for (landmark in result.multiHandLandmarks()[i].landmarkList) {
                // Draws the landmark.
                drawCircle(
                    landmark.x,
                    landmark.y,
                    if (isLeftHand) LEFT_HAND_LANDMARK_COLOR else RIGHT_HAND_LANDMARK_COLOR
                )
                //Log.d(TAG, "LandMark | x : ${landmark.x}, y : ${landmark.y}, z : ${landmark.z}")
                // Draws a hollow circle around the landmark.
                drawHollowCircle(
                    landmark.x,
                    landmark.y,
                    if (isLeftHand) LEFT_HAND_HOLLOW_CIRCLE_COLOR
                    else RIGHT_HAND_HOLLOW_CIRCLE_COLOR
                )
            }
        }
    }

    // OpenGL ES 프로그램을 삭제 함수
    fun release() {
        GLES20.glDeleteProgram(program)
    }

    // 랜드마크 간의 연결을 그리는 함수
    private fun drawConnections(
        handLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        colorArray: FloatArray
    ) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        for (c in Hands.HAND_CONNECTIONS) {
            val start = handLandmarkList[c.start()]
            val end = handLandmarkList[c.end()]
            val vertex = floatArrayOf(start.x, start.y, end.x, end.y)
            val vertexBuffer = ByteBuffer.allocateDirect(vertex.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex)
            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT,
                false, 0, vertexBuffer)
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
        }
    }

    // 랜드마크를 나타내는 채워진 원을 그리는 함수
    private fun drawCircle(x: Float, y: Float, colorArray: FloatArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        val vertexCount = NUM_SEGMENTS + 2
        val vertices = FloatArray(vertexCount * 3)
        vertices[0] = x
        vertices[1] = y
        vertices[2] = 0f
        for (i in 1 until vertexCount) {
            val angle = 2.0f * i * Math.PI.toFloat() / NUM_SEGMENTS
            val currentIndex = 3 * i
            vertices[currentIndex] = x + (LANDMARK_RADIUS * Math.cos(angle.toDouble())).toFloat()
            vertices[currentIndex + 1] =
                y + (LANDMARK_RADIUS * Math.sin(angle.toDouble())).toFloat()
            vertices[currentIndex + 2] = 0f
        }
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
            false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
    }

    // 랜드마크 주위에 빈 원을 그리는 함수
    private fun drawHollowCircle(x: Float, y: Float, colorArray: FloatArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        val vertexCount = NUM_SEGMENTS + 1
        val vertices = FloatArray(vertexCount * 3)
        for (i in 0 until vertexCount) {
            val angle = 2.0f * i * Math.PI.toFloat() / NUM_SEGMENTS
            val currentIndex = 3 * i
            vertices[currentIndex] =
                x + (HOLLOW_CIRCLE_RADIUS * Math.cos(angle.toDouble())).toFloat()
            vertices[currentIndex + 1] =
                y + (HOLLOW_CIRCLE_RADIUS * Math.sin(angle.toDouble())).toFloat()
            vertices[currentIndex + 2] = 0f
        }
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
            false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount)
    }

    companion object {
        private const val TAG = "HandsResultGlRenderer"
        private val LEFT_HAND_CONNECTION_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private val RIGHT_HAND_CONNECTION_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private const val CONNECTION_THICKNESS = 25.0f
        private val LEFT_HAND_HOLLOW_CIRCLE_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private val RIGHT_HAND_HOLLOW_CIRCLE_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private const val HOLLOW_CIRCLE_RADIUS = 0.01f
        private val LEFT_HAND_LANDMARK_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private val RIGHT_HAND_LANDMARK_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private const val LANDMARK_RADIUS = 0.008f
        private const val NUM_SEGMENTS = 120
        private const val VERTEX_SHADER = ("uniform mat4 uProjectionMatrix;\n"
                + "attribute vec4 vPosition;\n"
                + "void main() {\n"
                + "  gl_Position = uProjectionMatrix * vPosition;\n"
                + "}")
        private const val FRAGMENT_SHADER = ("precision mediump float;\n"
                + "uniform vec4 uColor;\n"
                + "void main() {\n"
                + "  gl_FragColor = uColor;\n"
                + "}")
    }

}