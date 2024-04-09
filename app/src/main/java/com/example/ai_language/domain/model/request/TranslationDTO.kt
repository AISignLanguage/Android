package com.example.ai_language.domain.model.request

import java.io.File

class TranslationDTO (
    val lang : String,
    val file : File,
    remotePath : String
)