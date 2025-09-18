package com.artiting.batch.job

import com.artiting.batch.service.MailService
import com.artiting.core.domain.Member
import com.artiting.core.domain.MemberArticle
import com.artiting.core.service.ArticleDomainService
import com.artiting.core.service.MemberArticleDomainService
import com.artiting.core.service.SubscribeDomainService
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
    private val subscribeDomainService: SubscribeDomainService,
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
        return JpaPagingItemReaderBuilder<Member>()
            .name("memberReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT m FROM Member m")
            .pageSize(10)
            .build()
    }

    @Bean
    fun sendMailProcessor(): ItemProcessor<Member, List<MemberArticle>> {
        return ItemProcessor { member ->
            val memberSubscribeCompanies = subscribeDomainService.findAllByMember(member).stream()
                .map { it.company }
                .toList()
            val yesterdayArticles = articleDomainService.findYesterdayByCompanies(memberSubscribeCompanies)

            log.info("Send Email to ${member.email}")
            mailService.sendMail(member, yesterdayArticles)

            yesterdayArticles.stream().map { MemberArticle(member = member, article = it) }.toList()
        }

    }

    @Bean
    fun mailWriter(): ItemWriter<List<MemberArticle>> {
        return ItemWriter { items ->
            items.flatten().forEach { memberArticleDomainService.save(it) }
        }
    }

}
