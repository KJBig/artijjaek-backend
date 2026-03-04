package com.artijjaek.mq.queue

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

@Component
class InMemoryMailQueueChannel : MailQueueChannel {
    private val queue = LinkedBlockingQueue<MailQueueMessage>()
    private val queuedOutboxIds = ConcurrentHashMap.newKeySet<Long>()

    override fun publish(message: MailQueueMessage) {
        val addedToSet = queuedOutboxIds.add(message.outboxId)
        if (!addedToSet) {
            return
        }
        queue.put(message)
    }

    override fun take(): MailQueueMessage {
        val message = queue.take()
        queuedOutboxIds.remove(message.outboxId)
        return message
    }
}
