package com.server.noati.ai

import com.server.noati.enums.CategoryType
import org.springframework.stereotype.Component

@Component
class CategoryAnalyzer {

    fun analyze(title: String, url: String): CategoryType {
        return CategoryType.BACKEND
    }

}