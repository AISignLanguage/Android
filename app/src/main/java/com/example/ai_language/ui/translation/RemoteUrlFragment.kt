package com.example.ai_language.ui.translation

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentRemoteUrlBinding
import com.example.ai_language.ui.translation.viewmodel.TranslationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RemoteUrlFragment : BaseFragment<FragmentRemoteUrlBinding>(R.layout.fragment_remote_url) {

    override fun setLayout() {
    }




}