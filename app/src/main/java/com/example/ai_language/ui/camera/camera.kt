package com.example.ai_language.ui.camera

import android.content.Intent
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentCameraBinding


class camera : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera) {
    override fun setLayout() {
        startActivity(Intent(requireActivity(),CameraPage::class.java))
    }

}

