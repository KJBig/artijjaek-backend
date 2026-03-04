package com.artijjaek.mq.queue

interface MailQueueChannel {
    fun publish(message: MailQueueMessage)
    fun take(): MailQueueMessage
}
