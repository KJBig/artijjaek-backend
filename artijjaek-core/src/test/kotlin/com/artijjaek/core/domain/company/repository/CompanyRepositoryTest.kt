package com.artijjaek.core.domain.company.repository

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CompanySortOption
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.repository.MemberRepository
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.repository.CompanySubscriptionRepository
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

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var companySubscriptionRepository: CompanySubscriptionRepository

    @AfterEach
    fun clear() {
        companySubscriptionRepository.deleteAll()
        memberRepository.deleteAll()
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
            blogUrl = "http://example1.com/blog",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val company2 = Company(
            nameKr = "회사2",
            nameEn = "Company2",
            logo = "http://example.com/logo2.png",
            blogUrl = "http://example1.com/blog",
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
            blogUrl = "http://example1.com/blog",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val company2 = Company(
            nameKr = "회사2",
            nameEn = "Company2",
            logo = "http://example.com/logo2.png",
            baseUrl = "http://example2.com",
            blogUrl = "http://example1.com/blog",
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
                blogUrl = "http://example${i}.com/blog",
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

    @Test
    @DisplayName("회사 목록을 KR_NAME 정렬 옵션으로 조회한다")
    fun findWithPageableOrderBySortOptionTest_KrName() {
        // given
        val companyA = Company(
            nameKr = "가회사",
            nameEn = "ACompany",
            logo = "http://example.com/logo-a.png",
            baseUrl = "http://example-a.com",
            blogUrl = "http://example-a.com/blog",
            crawlUrl = "http://example.com/crawl-a",
            crawlAvailability = true
        )
        val companyB = Company(
            nameKr = "나회사",
            nameEn = "BCompany",
            logo = "http://example.com/logo-b.png",
            baseUrl = "http://example-b.com",
            blogUrl = "http://example-b.com/blog",
            crawlUrl = "http://example.com/crawl-b",
            crawlAvailability = true
        )
        companyRepository.save(companyB)
        companyRepository.save(companyA)

        // when
        val result = companyRepository.findWithPageableOrderBySortOption(
            CompanySortOption.KR_NAME,
            PageRequest.of(0, 10)
        )

        // then
        Assertions.assertThat(result.content).hasSize(2)
        Assertions.assertThat(result.content[0].nameKr).isEqualTo("가회사")
        Assertions.assertThat(result.content[1].nameKr).isEqualTo("나회사")
    }

    @Test
    @DisplayName("회사 목록을 POPULARITY 정렬 옵션으로 조회한다")
    fun findWithPageableOrderBySortOptionTest_Popularity() {
        // given
        val popularCompany = companyRepository.save(
            Company(
                nameKr = "가회사",
                nameEn = "ACompany",
                logo = "http://example.com/logo-a.png",
                baseUrl = "http://example-a.com",
                blogUrl = "http://example-a.com/blog",
                crawlUrl = "http://example.com/crawl-a",
                crawlAvailability = true
            )
        )
        val normalCompany = companyRepository.save(
            Company(
                nameKr = "나회사",
                nameEn = "BCompany",
                logo = "http://example.com/logo-b.png",
                baseUrl = "http://example-b.com",
                blogUrl = "http://example-b.com/blog",
                crawlUrl = "http://example.com/crawl-b",
                crawlAvailability = true
            )
        )

        val firstMember = memberRepository.save(
            Member(
                email = "first@example.com",
                nickname = "first",
                uuidToken = "token-1",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        val secondMember = memberRepository.save(
            Member(
                email = "second@example.com",
                nickname = "second",
                uuidToken = "token-2",
                memberStatus = MemberStatus.ACTIVE
            )
        )

        companySubscriptionRepository.save(CompanySubscription(member = firstMember, company = popularCompany))
        companySubscriptionRepository.save(CompanySubscription(member = secondMember, company = popularCompany))
        companySubscriptionRepository.save(CompanySubscription(member = firstMember, company = normalCompany))

        // when
        val result = companyRepository.findWithPageableOrderBySortOption(
            CompanySortOption.POPULARITY,
            PageRequest.of(0, 10)
        )

        // then
        Assertions.assertThat(result.content).hasSize(2)
        Assertions.assertThat(result.content[0].id).isEqualTo(popularCompany.id)
        Assertions.assertThat(result.content[1].id).isEqualTo(normalCompany.id)
    }

}
