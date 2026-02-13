package com.artijjaek.batch.job

import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.entity.MemberArticle
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberArticleDomainService
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk

class TransportBatchTest {

    private val memberArticleDomainService = mockk<MemberArticleDomainService>(relaxed = true)
    private val companySubscriptionDomainService = mockk<CompanySubscriptionDomainService>()
    private val categorySubscriptionDomainService = mockk<CategorySubscriptionDomainService>()
    private val articleDomainService = mockk<ArticleDomainService>()
    private val mailService = mockk<MailService>(relaxed = true)

    private val config = TransportBatchConfig(
        mockk(),
        mockk(),
        mockk(),
        memberArticleDomainService,
        companySubscriptionDomainService,
        categorySubscriptionDomainService,
        articleDomainService,
        mailService
    )

    @Test
    @DisplayName("회원 리더를 생성하면 memberReader 이름으로 생성된다")
    fun memberReaderTest() {
        // given

        // when
        val reader = config.memberReader()

        // then
        assertThat(reader.name).isEqualTo("memberReader")
    }

    @Test
    @DisplayName("전송 프로세서는 구독 조건에 맞는 오늘 게시글을 MemberArticle 목록으로 변환한다")
    fun transportProcessorTest() {
        // given
        val member = createMember()
        val company = createCompany()
        val category = createCategory()
        val article1 = createArticle(company, category, "아티클1", "url1")
        val article2 = createArticle(company, category, "아티클2", "url2")
        val processor = config.transportProcessor()

        every { companySubscriptionDomainService.findAllByMemberFetchCompany(member) } returns listOf(
            CompanySubscription(member = member, company = company)
        )
        every { categorySubscriptionDomainService.findAllByMemberFetchCategory(member) } returns listOf(
            CategorySubscription(member = member, category = category)
        )
        every {
            articleDomainService.findTodayByCompaniesAndCategories(
                listOf(company),
                listOf(category)
            )
        } returns listOf(article1, article2)

        // when
        val result = processor.process(member)

        // then
        assertThat(result).hasSize(2)
        assertThat(result!![0].member).isEqualTo(member)
        assertThat(result[0].article).isEqualTo(article1)
        assertThat(result[1].article).isEqualTo(article2)
        verify(exactly = 1) { mailService.sendArticleMail(any(), any()) }
    }

    @Test
    @DisplayName("메일 라이터는 전달된 MemberArticle를 모두 저장한다")
    fun mailWriterTest() {
        // given
        val member = createMember()
        val company = createCompany()
        val category = createCategory()
        val article1 = createArticle(company, category, "아티클1", "url1")
        val article2 = createArticle(company, category, "아티클2", "url2")
        val memberArticle1 = MemberArticle(member = member, article = article1)
        val memberArticle2 = MemberArticle(member = member, article = article2)
        val writer = config.mailWriter()

        // when
        writer.write(Chunk(listOf(listOf(memberArticle1), listOf(memberArticle2))))

        // then
        verify(exactly = 1) { memberArticleDomainService.save(memberArticle1) }
        verify(exactly = 1) { memberArticleDomainService.save(memberArticle2) }
    }

    private fun createMember(): Member {
        return Member(
            email = "test@test.com",
            nickname = "tester",
            uuidToken = "uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )
    }

    private fun createCompany(): Company {
        return Company(
            nameKr = "올리브영",
            nameEn = "OLIVE YOUNG",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
    }

    private fun createCategory(): Category {
        return Category(
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
    }

    private fun createArticle(company: Company, category: Category, title: String, link: String): Article {
        return Article(
            title = title,
            link = link,
            company = company,
            category = category,
            description = null,
            image = null
        )
    }
}
