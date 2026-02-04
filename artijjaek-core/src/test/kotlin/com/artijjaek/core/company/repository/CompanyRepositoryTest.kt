package com.artijjaek.core.company.repository

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.repository.CompanyRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @AfterEach
    fun clear() {
        companyRepository.deleteAll()
    }

    @Test
    @DisplayName("ID 목록으로 회사 조회 또는 전체 조회 - ID 목록이 있을 경우")
    fun findAllOrByIdsTest_IdList() {
        // given
        val company1 = Company(
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example1.com",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val company2 = Company(
            nameKr = "회사2",
            nameEn = "Company2",
            logo = "http://example.com/logo2.png",
            baseUrl = "http://example2.com",
            crawlUrl = "http://example.com/crawl2",
            crawlAvailability = true
        )

        val saveCompany = companyRepository.save(company1)
        companyRepository.save(company2)

        val companyIds = listOf(saveCompany.id!!)


        // when
        val result = companyRepository.findAllOrByIds(companyIds)

        // then
        Assertions.assertThat(result.size).isEqualTo(1)
        Assertions.assertThat(result[0].nameKr).isEqualTo("회사1")
    }

    @Test
    @DisplayName("ID 목록으로 카테고리 조회 또는 전체 조회 - ID 목록이 없을 경우")
    fun findAllOrByIdsTest_All() {
        // given
        val company1 = Company(
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example1.com",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val company2 = Company(
            nameKr = "회사2",
            nameEn = "Company2",
            logo = "http://example.com/logo2.png",
            baseUrl = "http://example2.com",
            crawlUrl = "http://example.com/crawl2",
            crawlAvailability = true
        )

        companyRepository.save(company1)
        companyRepository.save(company2)


        // when
        val result = companyRepository.findAllOrByIds(emptyList())

        // then
        Assertions.assertThat(result.size).isEqualTo(2)
    }

    @Test
    @DisplayName("회사의 페이지를 조회한다")
    fun findWithPageableTest() {
        // given
        val companies = mutableListOf<Company>()
        for (i in 1..2) {
            val company = Company(
                nameKr = "회사${i}",
                nameEn = "Company${i}",
                logo = "http://example.com/logo${i}.png",
                baseUrl = "http://example${i}.com",
                crawlUrl = "http://example.com/crawl${i}",
                crawlAvailability = true
            )
            companies.add(company)
        }
        companyRepository.saveAll(companies)

        val pageRequest = PageRequest.of(0, 1)


        // when
        val result = companyRepository.findWithPageable(pageRequest)

        // then
        Assertions.assertThat(result.content.size).isEqualTo(1)
    }

}