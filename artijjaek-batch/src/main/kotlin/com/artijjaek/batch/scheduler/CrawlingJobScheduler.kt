package com.artijjaek.batch.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@EnableScheduling
class CrawlingJobScheduler(
    private val jobLauncher: JobLauncher,
    private val crawlingJob: Job,
    private val robotTxtJob: Job,
) {

    @Scheduled(cron = "0 0 0 * * *")
    fun runCrawlingJob() {
        val jobParameters = JobParametersBuilder()
            .addString("runTime", LocalDateTime.now().toString()) // 매 실행마다 다른 값
            .toJobParameters()

        jobLauncher.run(robotTxtJob, jobParameters)
        jobLauncher.run(crawlingJob, jobParameters)
    }
}
