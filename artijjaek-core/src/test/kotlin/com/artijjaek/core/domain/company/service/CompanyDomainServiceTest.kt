package com.artijjaek.core.domain.company.service

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CompanySortOption
import com.artijjaek.core.domain.company.repository.CompanyRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CompanyDomainServiceTest {

    @InjectMockKs
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var companyRepository: CompanyRepository

    @Test
    @DisplayName("회사 목록을 구독자 수 내림차순, 한글 이름 오름차순으로 페이지 조회할 수 있다")
    fun findWithPageableOrderBySortOptionTest() {
        // given
        val sortOption = CompanySortOption.KR_NAME
        val pageRequest = PageRequest.of(0, 10)
        val page = mockk<Page<Company>>()
        every { companyRepository.findWithPageableOrderBySortOption(sortOption, pageRequest) }.returns(page)

        // when
        val result = companyDomainService.findWithPageableOrderBySortOption(sortOption, pageRequest)

        // then
        assertThat(result).isEqualTo(page)
        verify(exactly = 1) { companyRepository.findWithPageableOrderBySortOption(sortOption, pageRequest) }
    }
}
