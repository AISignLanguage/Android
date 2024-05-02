package com.example.ai_language.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentHomeMenuBinding
import com.example.ai_language.ui.extensions.setSrcVolunteerImage
import com.example.ai_language.ui.map.MapActivity
import com.example.ai_language.ui.translation.TranslationActivity
import com.example.ai_language.ui.vibration.SoundDetectionService
import io.reactivex.rxjava3.disposables.CompositeDisposable

class HomeMenuFragment : BaseFragment <FragmentHomeMenuBinding>(R.layout.fragment_home_menu) {
    private var isServiceRunning = false // 서비스 실행 여부를 추적하는 변수
    private val disposables = CompositeDisposable()

    override fun setLayout() {
        setOnClickImageButton()
    }
    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
    private fun setOnClickImageButton() {
        with(binding) {
            ibVib.setImageResource(R.drawable.ic_vibration_vector_off)
            ivPoster.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_menu_to_home)
            }
            ibMap.setOnClickListener {
                startActivity(Intent(requireActivity(), MapActivity::class.java))
            }
            ibDic.setOnClickListener {
                //사전
            }
            ibVib.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
                }
                    if (isServiceRunning) {
                        stopDetectionService()
                        ibVib.setImageResource(R.drawable.ic_vibration_vector_off)
                    } else {
                        startDetectionService()
                        ibVib.setImageResource(R.drawable.ic_vibration_vector)
                    }

            }
            ibYoutube.setOnClickListener {
                startActivity(Intent(requireActivity(), TranslationActivity::class.java))
            }
        }
    }

    private fun startDetectionService() {
        val serviceIntent = Intent(requireActivity(), SoundDetectionService::class.java)
        if (!isServiceRunning) {
            ContextCompat.startForegroundService(requireActivity(), serviceIntent)
            isServiceRunning = true
        } else {
            Toast.makeText(requireActivity(), "서비스가 이미 실행 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopDetectionService() {
        val serviceIntent = Intent(requireActivity(), SoundDetectionService::class.java)
        if (isServiceRunning) {
            requireContext().stopService(serviceIntent)
            isServiceRunning = false
        } else {
            Toast.makeText(requireActivity(), "서비스가 실행 중이지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

}