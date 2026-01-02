package com.artijjaek.core.webhook

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.inquiry.entity.Inquiry

interface WebHookService {
    fun sendNewArticleMessage(newArticles: List<Article>)
    fun sendNewInquiryMessage(newInquiry: Inquiry)
    fun sendCategoryAllocateMessage(articles: List<Article>, categories: Map<Int, Category>)
}