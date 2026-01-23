package com.artijjaek.api.service

import com.artijjaek.api.dto.request.InquiryRequest
import com.artijjaek.core.domain.inquiry.entity.Inquiry
import com.artijjaek.core.domain.inquiry.service.InquiryDomainService
import com.artijjaek.core.webhook.WebHookService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InquiryService(
    private val inquiryDomainService: InquiryDomainService,
    private val webHookService: WebHookService,
) {

    @Transactional
    fun saveInquiry(request: InquiryRequest) {
        val inquiry = Inquiry(email = request.email, content = request.content)
        val newInquiry = inquiryDomainService.saveInquiry(inquiry)
        webHookService.sendNewInquiryMessage(newInquiry)
    }

}