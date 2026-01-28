package com.artijjaek.core.webhook

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.inquiry.entity.Inquiry

interface WebHookService {
    fun sendNewArticleMessage(newArticles: List<ArticleAlertDto>)
    fun sendNewInquiryMessage(newInquiry: Inquiry)
    fun sendCategoryAllocateMessage(articles: List<ArticleAlertDto>, categories: Map<Int, Category>)
}