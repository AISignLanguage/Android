package com.example.ai_language.ui.translation

import android.util.Log
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentYoutubeUrlBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class YoutubeUrlFragment : BaseFragment<FragmentYoutubeUrlBinding>(R.layout.fragment_youtube_url) {
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var isPlayerReady = false

    override fun setLayout() {
        initYouTubePlayerView()
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
}
