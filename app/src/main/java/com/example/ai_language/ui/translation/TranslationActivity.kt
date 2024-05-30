package com.example.ai_language.ui.translation

import androidx.activity.viewModels
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityTranslationBinding
import com.example.ai_language.ui.translation.viewmodel.TranslationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslationActivity : BaseActivity<ActivityTranslationBinding> (R.layout.activity_translation) {

    private val translationViewModel by viewModels<TranslationViewModel>()
    override fun setLayout() {
        supportFragmentManager.beginTransaction().apply {
            add(binding.fvUrlFragment.id, YoutubeUrlFragment()) // 'fragment_container'는 FrameLayout의 ID입니다.
            commit() // 트랜잭션 커밋
        }
    }


}