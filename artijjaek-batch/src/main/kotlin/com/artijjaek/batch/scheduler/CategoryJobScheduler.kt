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
class CategoryJobScheduler(
    private val jobLauncher: JobLauncher,
    private val allocateCategoryJob: Job,
) {

    @Scheduled(cron = "0 0 3 * * *")
    fun runCategoryJob() {
        val jobParameters = JobParametersBuilder()
            .addString("runTime", LocalDateTime.now().toString()) // 매 실행마다 다른 값
            .toJobParameters()

        jobLauncher.run(allocateCategoryJob, jobParameters)
    }
}
