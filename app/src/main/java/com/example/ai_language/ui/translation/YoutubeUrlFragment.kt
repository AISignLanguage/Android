package com.example.ai_language.ui.translation

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ai_language.BuildConfig
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentYoutubeUrlBinding
import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.ui.translation.viewmodel.TranslationViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class YoutubeUrlFragment : BaseFragment<FragmentYoutubeUrlBinding>(R.layout.fragment_youtube_url) {
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var isPlayerReady = false
    private var link = ""
    private val translationViewModel by viewModels<TranslationViewModel>()

    override fun setLayout() {
        initYouTubePlayerView()
        viewModelScope()
    }

    private fun initYouTubePlayerView() {
        youTubePlayerView = binding.yvYoutubePlay
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.d("youtube", "Player is ready")
                isPlayerReady = true
            }
        })

        binding.btnYoutubeLink.setOnClickListener {
            if (isPlayerReady) {
                val url = binding.etYoutubeUrl.text.toString()
                translationViewModel.getWavUrl(WavUrlResponse(url))
                val videoId = extractYouTubeVideoId(url)
                Log.d("youtube", "버튼, $videoId")

                if (videoId != null) {
                    // YouTubePlayerCallback 인터페이스를 명시적으로 구현
                    youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    })
                } else {
                    binding.etYoutubeUrl.setText("URL 주소 오류")
                }
            } else {
                Log.d("youtube", "Player not ready")
            }
        }
    }

    private fun extractYouTubeVideoId(youtubeUrl: String): String? {
        // 이 함수는 사용자가 입력한 URL에서 YouTube 동영상 ID를 추출합니다.
        val queryStart = youtubeUrl.indexOf("?v=") + 3
        val ampersandPosition = youtubeUrl.indexOf("&", queryStart)
        val singleVideoIdLength = 11  // YouTube 동영상 ID의 표준 길이

        return when {
            youtubeUrl.contains("youtu.be/") -> {
                youtubeUrl.substringAfterLast("youtu.be/")
                    .takeIf { it.length >= singleVideoIdLength }
                    ?.substring(0, singleVideoIdLength)
            }

            queryStart > 2 -> {
                if (ampersandPosition > 0) {
                    youtubeUrl.substring(queryStart, ampersandPosition)
                } else {
                    youtubeUrl.substring(queryStart)
                }
            }

            youtubeUrl.contains("/embed/") -> {
                youtubeUrl.substringAfterLast("/embed/")
                    .takeIf { it.length >= singleVideoIdLength }
                    ?.substring(0, singleVideoIdLength)
            }

            else -> null
        }
    }

    private fun viewModelScope(){
        sendRemote()
        resultText()
        onClickRemote()
    }
    private fun onClickRemote(){
        binding.btnSendRemoteApi.setOnClickListener {
            val txt = binding.etRemoteFileInfo.text.toString()
            translationViewModel.postTextByRemoteFile(BuildConfig.Speech_to_Text_key_id, BuildConfig.Speech_to_Text_key_secret,"ko",txt)
        }
        binding.btnGo.setOnClickListener {
            translationViewModel.getTextFileBySpeechFlowApi(BuildConfig.Speech_to_Text_key_id,BuildConfig.Speech_to_Text_key_secret,link,4)
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
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                translationViewModel.wavUrl.collectLatest {
                    val trUrl = it.url.replace("/static/static","/static")
                    binding.etRemoteFileInfo.setText("http://34.64.212.107:8000"+trUrl)
                    link = it.url
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
