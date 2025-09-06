package com.noati.batch.job

import com.noati.batch.service.MailService
import com.noati.core.domain.Member
import com.noati.core.service.ArticleDomainService
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
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
    private val articleDomainService: ArticleDomainService,
    private val mailService: MailService,

    ) {

    @Bean
    fun mailJob(): Job {
        return JobBuilder("mailJob", jobRepository)
            .start(mailStep())
            .build()
    }

    @Bean
    fun mailStep(): Step {
        return StepBuilder("mailStep", jobRepository)
            .chunk<Member, Unit>(10, transactionManager)
            .reader(memberReader())
            .processor(sendMailProcessor())
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
    fun sendMailProcessor(): ItemProcessor<Member, Unit> {
        val yesterdayArticles = articleDomainService.findYesterdayArticle()
        return ItemProcessor { member ->
            mailService.sendMail(member, yesterdayArticles)
        }

    }

}
