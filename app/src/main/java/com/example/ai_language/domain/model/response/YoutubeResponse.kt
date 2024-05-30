package com.example.ai_language.domain.model.response

data class YoutubeResponse(
    val items: List<VideoItem> = emptyList() // 동영상 목록. 각각의 동영상 정보가 VideoItem 객체로 표현됩니다.
)

// 각각의 동영상 정보를 담는 클래스입니다.
data class VideoItem(
    val snippet: VideoSnippet = VideoSnippet(), // 동영상의 기본 정보(제목, 설명 등)를 담는 객체
    val statistics: VideoStatistics = VideoStatistics()// 동영상의 통계 정보(조회수, 좋아요 수 등)를 담는 객체
)

// 동영상의 기본 정보를 나타내는 클래스입니다.
data class VideoSnippet(
    val title: String="", // 동영상 제목
    val description: String="", // 동영상 설명
    val publishedAt: String="", // 동영상 게시 날짜 및 시간 (ISO 8601 형식)
    val channelId: String="", // 동영상을 게시한 채널의 ID
    val channelTitle: String="" // 동영상을 게시한 채널의 제목
)

// 동영상의 통계 정보를 나타내는 클래스입니다.
data class VideoStatistics(
    val viewCount: String="", // 동영상의 조회수
    val likeCount: String="", // 동영상의 좋아요 수
    val commentCount: String="" // 동영상에 달린 댓글 수
)