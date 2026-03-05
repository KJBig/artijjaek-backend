package com.artijjaek.core.domain.subscription.repsitory

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.repository.CompanyRepository
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.repository.MemberRepository
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.repository.CompanySubscriptionRepository
import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason
import com.artijjaek.core.domain.unsubscription.repository.UnsubscriptionRepository
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

    @Autowired
    lateinit var unsubscriptionRepository: UnsubscriptionRepository

    @AfterEach
    fun clear() {
        companySubscriptionRepository.deleteAll()
        unsubscriptionRepository.deleteAll()
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

    @Test
    @DisplayName("탈퇴한 사용자의 회사 구독 기록은 인기 회사 집계에서 제외된다")
    fun findTopSubscribedCompaniesWithTies_excludeUnsubscriptionTest() {
        // given
        val companyA = companyRepository.save(
            Company(
                nameKr = "회사A",
                nameEn = "CompanyA",
                logo = "http://example.com/logo-a.png",
                baseUrl = "http://example.com/a",
                blogUrl = "http://example.com/blog-a",
                crawlUrl = "http://example.com/crawl-a",
                crawlAvailability = true
            )
        )
        val companyB = companyRepository.save(
            Company(
                nameKr = "회사B",
                nameEn = "CompanyB",
                logo = "http://example.com/logo-b.png",
                baseUrl = "http://example.com/b",
                blogUrl = "http://example.com/blog-b",
                crawlUrl = "http://example.com/crawl-b",
                crawlAvailability = true
            )
        )

        val activeMember = memberRepository.save(
            Member(
                email = "active@example.com",
                nickname = "active",
                uuidToken = "uuid-active",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        val unsubscribedMember = memberRepository.save(
            Member(
                email = "bye@example.com",
                nickname = "bye",
                uuidToken = "uuid-bye",
                memberStatus = MemberStatus.DELETED
            )
        )

        companySubscriptionRepository.save(CompanySubscription(member = activeMember, company = companyB))
        companySubscriptionRepository.save(CompanySubscription(member = unsubscribedMember, company = companyA))

        unsubscriptionRepository.save(
            Unsubscription(
                member = unsubscribedMember,
                email = unsubscribedMember.email,
                reason = UnSubscriptionReason.ETC,
                detail = "테스트"
            )
        )

        // when
        val result = companySubscriptionRepository.findTopSubscribedCompaniesWithTies(5)

        // then
        Assertions.assertThat(result).hasSize(1)
        Assertions.assertThat(result[0].companyId).isEqualTo(companyB.id)
        Assertions.assertThat(result[0].subscriberCount).isEqualTo(1L)
    }

}
