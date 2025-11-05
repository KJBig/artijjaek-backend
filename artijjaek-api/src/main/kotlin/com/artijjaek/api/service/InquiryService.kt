package com.artijjaek.api.service

import com.artijjaek.api.dto.request.InquiryRequest
import com.artijjaek.core.domain.inquiry.entity.Inquiry
import com.artijjaek.core.domain.inquiry.service.InquiryDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InquiryService(
    private val inquiryDomainService: InquiryDomainService,
) {

    @Transactional
    fun saveInquiry(request: InquiryRequest) {
        val inquiry = Inquiry(content = request.content)
        inquiryDomainService.saveInquiry(inquiry)
    }

}