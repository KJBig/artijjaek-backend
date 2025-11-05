package com.artijjaek.core.domain.inquiry.service

import com.artijjaek.core.domain.inquiry.entity.Inquiry
import com.artijjaek.core.domain.inquiry.repository.InquiryRepository
import org.springframework.stereotype.Service

@Service
class InquiryDomainService(
    private val inquiryRepository: InquiryRepository,
) {

    fun saveInquiry(unsubscription: Inquiry): Inquiry {
        return inquiryRepository.save(unsubscription)
    }

}