package com.example.ai_language.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ai_language.R

class Page1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_page1, container, false)
        view.setOnClickListener {
            val posterFragment = PosterFragment()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.fade_in, // 진입 애니메이션
                    R.anim.fade_out, // 종료 애니메이션
                    R.anim.fade_in, // 팝 진입 애니메이션 (뒤로 가기 시)
                    R.anim.fade_out // 팝 종료 애니메이션 (뒤로 가기 시)
                )
                replace(R.id.fr_all, posterFragment)
                addToBackStack(null)
                commit()
            }
        }
        return view
    }
}