package com.example.ai_language.ui.audioclassification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.ai_language.R
import com.example.ai_language.databinding.FragmentAudioBinding
import org.tensorflow.lite.support.label.Category

interface AudioClassificationListener {
    fun onError(error: String)
    fun onResult(results: List<Category>, inferenceTime: Long)
}

class AudioFragment : Fragment() {
    private var _fragmentBinding: FragmentAudioBinding? = null
    private val fragmentAudioBinding get() = _fragmentBinding!!
    private val adapter by lazy { ProbabilitiesAdapter() }

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.RECORD_AUDIO)

    private lateinit var audioHelper: AudioClassificationHelper

    // 모든 필수 권한이 부여되었는지 확인하는 메서드
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청 처리 리스너
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "권한이 허용되었습니다.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            ContextCompat.checkSelfPermission(  // 권한이 허용된 경우
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> { }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // 오디오 분류 결과 및 오류 처리를 위한 리스너
    private val audioClassificationListener = object : AudioClassificationListener {

        // 오디오 분류 결과 및 오류 처리를 위한 리스너
        override fun onResult(results: List<Category>, inferenceTime: Long) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                adapter.notifyDataSetChanged()
                fragmentAudioBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", inferenceTime)
            }
        }

        // 오류 메시지를 받아 사용자에게 알림
        override fun onError(error: String) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = FragmentAudioBinding.inflate(inflater, container, false)
        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        fragmentAudioBinding.recyclerView.adapter = adapter

        // Adapter 설정
        audioHelper = AudioClassificationHelper(
            requireContext(),
            audioClassificationListener
        )

        // 모델 선택
        fragmentAudioBinding.bottomSheetLayout.modelSelector.setOnCheckedChangeListener(
            object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        R.id.yamnet -> {
                            audioHelper.stopAudioClassification()
                            audioHelper.currentModel = AudioClassificationHelper.YAMNET_MODEL
                            audioHelper.initClassifier()
                        }
                        R.id.speech_command -> {
                            audioHelper.stopAudioClassification()
                            audioHelper.currentModel = AudioClassificationHelper.SPEECH_COMMAND_MODEL
                            audioHelper.initClassifier()
                        }
                    }
                }
            })

        // 오디오 분류 설정을 변경, 오디오 겹치는 부분의 양을 조절
        fragmentAudioBinding.bottomSheetLayout.spinnerOverlap.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    audioHelper.stopAudioClassification()
                    audioHelper.overlap = 0.25f * position
                    audioHelper.startAudioClassification()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }
            }

        // 반환되는 결과의 최대 수를 조절 (1 ~ 5)
        fragmentAudioBinding.bottomSheetLayout.resultsMinus.setOnClickListener {
            if (audioHelper.numOfResults > 1) {
                audioHelper.numOfResults--
                audioHelper.stopAudioClassification()
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.resultsValue.text =
                    audioHelper.numOfResults.toString()
            }
        }
        fragmentAudioBinding.bottomSheetLayout.resultsPlus.setOnClickListener {
            if (audioHelper.numOfResults < 5) {
                audioHelper.numOfResults++
                audioHelper.stopAudioClassification()
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.resultsValue.text =
                    audioHelper.numOfResults.toString()
            }
        }

        // 분류 결과로 반환되는 레이블의 신뢰도 임계값을 조절
        fragmentAudioBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
            if (audioHelper.classificationThreshold >= 0.2) {
                audioHelper.stopAudioClassification()
                audioHelper.classificationThreshold -= 0.1f
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.thresholdValue.text =
                    String.format("%.2f", audioHelper.classificationThreshold)
            }
        }
        fragmentAudioBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
            if (audioHelper.classificationThreshold <= 0.8) {
                audioHelper.stopAudioClassification()
                audioHelper.classificationThreshold += 0.1f
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.thresholdValue.text =
                    String.format("%.2f", audioHelper.classificationThreshold)
            }
        }

        // 분류에 사용되는 스레드 수를 조절
        fragmentAudioBinding.bottomSheetLayout.threadsMinus.setOnClickListener {
            if (audioHelper.numThreads > 1) {
                audioHelper.stopAudioClassification()
                audioHelper.numThreads--
                fragmentAudioBinding.bottomSheetLayout.threadsValue.text = audioHelper
                    .numThreads
                    .toString()
                audioHelper.initClassifier()
            }
        }
        fragmentAudioBinding.bottomSheetLayout.threadsPlus.setOnClickListener {
            if (audioHelper.numThreads < 4) {
                audioHelper.stopAudioClassification()
                audioHelper.numThreads++
                fragmentAudioBinding.bottomSheetLayout.threadsValue.text = audioHelper
                    .numThreads
                    .toString()
                audioHelper.initClassifier()
            }
        }

        // 하드웨어 설정 변경, 선택된 하드웨어에 따라 오디오 분류를 초기화
        fragmentAudioBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    audioHelper.stopAudioClassification()
                    audioHelper.currentDelegate = position
                    audioHelper.initClassifier()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    /* no op */
                }
            }

        // 기본값 설정
        fragmentAudioBinding.bottomSheetLayout.spinnerOverlap.setSelection(2, false)
        fragmentAudioBinding.bottomSheetLayout.spinnerDelegate.setSelection(0, false)
    }

    override fun onResume() {
        super.onResume()

        // 권한 확인 여부 후 없으면 확인 프래그먼트로 이동
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> { }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO) // 권한 요청 다이얼로그
            }
        }

        // 오디오 분류 초기화 및 실행
        if (::audioHelper.isInitialized ) {
            audioHelper.startAudioClassification()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::audioHelper.isInitialized ) {
            audioHelper.stopAudioClassification()
        }
    }

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
    }
}