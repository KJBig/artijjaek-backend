package com.artijjaek.batch.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MailJobScheduler(
    private val jobLauncher: JobLauncher,
    private val mailJob: Job
) {

    @Scheduled(cron = "0 0 7 * * *")
    fun runMailJob() {
        val jobParameters = JobParametersBuilder()
            .addString("runTime", LocalDateTime.now().toString())
            .toJobParameters()

        jobLauncher.run(mailJob, jobParameters)
    }
}
