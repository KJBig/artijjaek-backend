package com.artijjaek.mail_worker.worker

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MailWorkerMonitoringScheduler(
    private val alertService: EmailOutboxAlertService,
) {

    @Scheduled(fixedDelay = 60000)
    fun checkBacklog() {
        alertService.checkBacklog()
    }
}
