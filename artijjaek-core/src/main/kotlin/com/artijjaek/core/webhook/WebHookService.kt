package com.artijjaek.core.webhook

import com.artijjaek.core.common.mail.dto.ArticleMailDto
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.inquiry.entity.Inquiry

interface WebHookService {
    fun sendNewArticleMessage(newArticles: List<ArticleMailDto>)
    fun sendNewInquiryMessage(newInquiry: Inquiry)
    fun sendCategoryAllocateMessage(articles: List<ArticleMailDto>, categories: Map<Int, Category>)
}