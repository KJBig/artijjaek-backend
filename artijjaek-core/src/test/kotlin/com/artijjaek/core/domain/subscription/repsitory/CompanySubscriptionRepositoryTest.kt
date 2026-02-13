package com.artijjaek.core.domain.subscription.repsitory

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.repository.CompanyRepository
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test


@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class CompanySubscriptionRepositoryTest {

    @Autowired
    lateinit var companySubscriptionRepository: CompanySubscriptionRepository

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @AfterEach
    fun clear() {
        companySubscriptionRepository.deleteAll()
        memberRepository.deleteAll()
        companyRepository.deleteAll()
    }

    @Test
    @DisplayName("멤버로 회사 구독 조회")
    fun findAllByMemberFetchCompanyTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
        val saveCompany = companyRepository.save(company)

        val member = Member(
            email = "test@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )
        val saveMember = memberRepository.save(member)

        val companySubscription = CompanySubscription(member = saveMember, company = saveCompany)
        companySubscriptionRepository.save(companySubscription)


        // when
        val result = companySubscriptionRepository.findAllByMemberFetchCompany(saveMember)


        // then
        Assertions.assertThat(result.size).isEqualTo(1)
        Assertions.assertThat(result[0].company.nameKr).isEqualTo("회사")
    }

}