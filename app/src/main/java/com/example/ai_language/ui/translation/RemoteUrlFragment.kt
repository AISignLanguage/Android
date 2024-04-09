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
    private val translationViewModel by viewModels<TranslationViewModel>()
    private var link = ""
    override fun setLayout() {
        viewModelScope()
    }

    private fun viewModelScope(){
        sendRemote()
        resultText()
        onClickRemote()
    }

    private fun onClickRemote(){
        binding.btnSendRemoteApi.setOnClickListener {
            val txt = binding.etRemoteFileInfo.text.toString()
            translationViewModel.postTextByRemoteFile("z45ijode90Uu2Z9R","NnYgsPwKxqAzNkIl","en",txt)
        }
        binding.btnGo.setOnClickListener {
            translationViewModel.getTextFileBySpeechFlowApi("z45ijode90Uu2Z9R","NnYgsPwKxqAzNkIl",link,4)
        }
    }
    private fun sendRemote(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                translationViewModel.remote.collectLatest {
                    Log.d("Remote","${it.code} : ${it.msg} , ${it.taskId}")
                    binding.tvFileKey.text = it.taskId
                    link = it.taskId
                }
            }
        }
    }

    private fun resultText(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                translationViewModel.result.collectLatest {
                    Log.d("Remote","${it.code} : ${it.msg}")
                    if(it.code.toString() == "11001") {
                        binding.tvResult.text = "서버에서 번역이 진행중 입니다. 잠시 후 다시 go 버튼을 눌러주세요"
                    }
                    else if(it.code.toString() == "11000"){
                        binding.tvResult.text = it.result
                    }
                    else{
                        binding.tvResult.text = "error code : ${it.code} : ${it.msg}"
                    }
                }
            }
        }
    }

}