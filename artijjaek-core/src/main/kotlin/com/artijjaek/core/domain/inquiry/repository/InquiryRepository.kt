package com.artijjaek.core.domain.inquiry.repository

import com.artijjaek.core.domain.inquiry.entity.Inquiry
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long> {
}