package com.artijjaek.batch.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TransportJobScheduler(
    private val jobLauncher: JobLauncher,
    private val transportJob: Job
) {

    @Scheduled(cron = "0 0 12 * * *")
    fun transportMailJob() {
        val jobParameters = JobParametersBuilder()
            .addString("runTime", LocalDateTime.now().toString())
            .toJobParameters()

        jobLauncher.run(transportJob, jobParameters)
    }
}
