package com.artijjaek.api.service

import com.artijjaek.api.dto.request.InquiryRequest
import com.artijjaek.core.domain.inquiry.service.InquiryDomainService
import com.artijjaek.core.webhook.WebHookService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class InquiryServiceTest {

    @InjectMockKs
    lateinit var inquiryService: InquiryService

    @MockK
    lateinit var inquiryDomainService: InquiryDomainService

    @MockK
    lateinit var webHookService: WebHookService

    @Test
    @DisplayName("누구나 문의를 남길 수 있다")
    fun saveInquiry() {
        // given
        val request = InquiryRequest(
            email = "test@example.com",
            content = "some inquiry content"
        )

        every { inquiryDomainService.saveInquiry(any()) }.returns(mockk())
        justRun { webHookService.sendNewInquiryMessage(any()) }

        // when
        inquiryService.saveInquiry(request)

        // then
        verify { inquiryDomainService.saveInquiry(any()) }
        verify { webHookService.sendNewInquiryMessage(any()) }
    }
    
}