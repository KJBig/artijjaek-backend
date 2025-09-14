package com.noati.batch.job

import com.noati.batch.service.RobotsTxtCheckService
import com.noati.core.domain.Company
import com.noati.core.service.CompanyDomainService
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
class RobotTxtBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val companyDomainService: CompanyDomainService,
    private val robotsTxtCheckService: RobotsTxtCheckService,
) {

    private val log = LoggerFactory.getLogger(RobotTxtBatchConfig::class.java)

    @Bean
    fun robotTxtJob(): Job {
        return JobBuilder("robotTxtJob", jobRepository)
            .start(checkRobotTxtStep())
            .build()
    }

    @Bean
    fun checkRobotTxtStep(): Step {
        return StepBuilder("checkRobotTxtStep", jobRepository)
            .chunk<Company, Company>(10, transactionManager)
            .reader(robotTextCompanyReader())
            .processor(checkRobotTxtProcessor())
            .writer(companyWriter())
            .build()
    }

    @Bean
    fun robotTextCompanyReader(): JpaPagingItemReader<Company> {
        return JpaPagingItemReaderBuilder<Company>()
            .name("robotTextCompanyReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM Company c")
            .pageSize(10)
            .build()
    }

    @Bean
    fun checkRobotTxtProcessor(): ItemProcessor<Company, Company> {
        return ItemProcessor { company ->
            val crawlAllowed = robotsTxtCheckService.isCrawlAllowed(company)
            if (!crawlAllowed) {
                printDetectLog(company)
            }
            company.chaneCrawlAvailability(crawlAllowed)
            company
        }

    }

    private fun printDetectLog(company: Company) {
        log.info("[${company.nameKr}] change crawl availability : false")
    }

    @Bean
    fun companyWriter(): ItemWriter<Company> {
        return ItemWriter { items ->
            items.forEach { companyDomainService.save(it) }
        }
    }
}