package com.artijjaek.batch.job

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.entity.MemberArticle
import com.artijjaek.core.domain.member.service.MemberArticleDomainService
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
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
class TransportBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val memberArticleDomainService: MemberArticleDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
    private val categorySubscriptionDomainService: CategorySubscriptionDomainService,
    private val articleDomainService: ArticleDomainService,
    private val mailService: MailService,

    ) {

    private val log = LoggerFactory.getLogger(TransportBatchConfig::class.java)

    @Bean
    fun transportJob(): Job {
        return JobBuilder("transportJob", jobRepository)
            .start(transportStep())
            .build()
    }

    @Bean
    fun transportStep(): Step {
        return StepBuilder("transportStep", jobRepository)
            .chunk<Member, List<MemberArticle>>(10, transactionManager)
            .reader(memberReader())
            .processor(transportProcessor())
            .writer(mailWriter())
            .build()
    }

    @Bean
    fun memberReader(): JpaPagingItemReader<Member> {
        return JpaPagingItemReaderBuilder<Member>()
            .name("memberReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT m FROM Member m")
            .pageSize(10)
            .build()
    }

    @Bean
    fun transportProcessor(): ItemProcessor<Member, List<MemberArticle>> {
        return ItemProcessor { member ->
            val memberSubscribeCompanies = companySubscriptionDomainService.findAllByMember(member).stream()
                .map { it.company }
                .toList()

            val memberSubscribeCategories = categorySubscriptionDomainService.findAllByMember(member).stream()
                .map { it.category }
                .toList()

            val todayArticles = articleDomainService.findTodayByCompaniesAndCategories(
                memberSubscribeCompanies,
                memberSubscribeCategories
            )

            if (todayArticles.isEmpty()) {
                return@ItemProcessor emptyList()
            }

            val articles = todayArticles.map { ArticleAlertDto.from(it) }

            mailService.sendArticleMail(MemberAlertDto.from(member), articles)

            todayArticles.stream().map { MemberArticle(member = member, article = it) }.toList()
        }

    }

    @Bean
    fun mailWriter(): ItemWriter<List<MemberArticle>> {
        return ItemWriter { items ->
            val memberArticles = items.flatten()

            if (memberArticles.isEmpty()) return@ItemWriter

            val memberCount = memberArticles.map { it.member.id }.distinct().count()
            val articleCount = memberArticles.size

            log.info(
                "[TransportBatch] chunk processed - members={}, articles={}",
                memberCount,
                articleCount
            )

            items.flatten().forEach { memberArticleDomainService.save(it) }
        }
    }

}
