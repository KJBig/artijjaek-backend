package com.artijjaek.core.webhook

import com.artijjaek.core.domain.article.entity.Article

interface WebHookService {
    fun sendNewArticleMessage(newArticles: List<Article>)
}