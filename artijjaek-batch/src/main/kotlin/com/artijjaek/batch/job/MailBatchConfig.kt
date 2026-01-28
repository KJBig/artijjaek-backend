package com.artijjaek.batch.job

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.entity.MemberArticle
import com.artijjaek.core.domain.member.service.MemberArticleDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class MailBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val memberArticleDomainService: MemberArticleDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
    private val articleDomainService: ArticleDomainService,
    private val mailService: MailService,

    ) {

    private val log = LoggerFactory.getLogger(MailBatchConfig::class.java)

    @Bean
    fun mailJob(): Job {
        return JobBuilder("mailJob", jobRepository)
            .start(mailStep())
            .build()
    }

    @Bean
    fun mailStep(): Step {
        return StepBuilder("mailStep", jobRepository)
            .chunk<Member, List<MemberArticle>>(10, transactionManager)
            .reader(memberReader())
            .processor(sendMailProcessor())
            .writer(mailWriter())
            .build()
    }

    @Bean
    fun memberReader(): JpaPagingItemReader<Member> {
        val query = """
            SELECT DISTINCT m 
            FROM Member m 
            JOIN MemberArticle ma ON ma.member = m
            WHERE ma.createdAt BETWEEN CURRENT_DATE AND CURRENT_TIMESTAMP
        """.trimIndent()

        return JpaPagingItemReaderBuilder<Member>()
            .name("memberReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(query)
            .pageSize(10)
            .build()
    }

    @Bean
    fun sendMailProcessor(): ItemProcessor<Member, List<MemberArticle>> {
        return ItemProcessor { member ->
            val memberSubscribeCompanies = companySubscriptionDomainService.findAllByMember(member).stream()
                .map { it.company }
                .toList()
            val todayArticles = articleDomainService.findTodayByCompanies(memberSubscribeCompanies)

            if (todayArticles.isEmpty()) {
                log.info("No new articles for ${member.email}, skipping email")
                return@ItemProcessor emptyList()
            }

            val articleDatas = todayArticles.map { ArticleAlertDto.from(it) }

            log.info("Send Email to ${member.email}")

            mailService.sendArticleMail(MemberAlertDto.from(member), articleDatas)

            todayArticles.stream().map { MemberArticle(member = member, article = it) }.toList()
        }

    }

    @Bean
    fun mailWriter(): ItemWriter<List<MemberArticle>> {
        return ItemWriter { items ->
            items.flatten().forEach { memberArticleDomainService.save(it) }
        }
    }

}
