package com.noati.batch.ai

import com.noati.core.enums.CategoryType
import org.springframework.stereotype.Component

@Component
class GeminiClient {

    fun analyzeArticleCategory(title: String, url: String): CategoryType {
        return CategoryType.BACKEND
    }

}