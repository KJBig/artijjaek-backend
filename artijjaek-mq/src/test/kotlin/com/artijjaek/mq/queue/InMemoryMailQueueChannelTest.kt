package com.artijjaek.mq.queue

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class InMemoryMailQueueChannelTest {

    private val queueChannel = InMemoryMailQueueChannel()

    @Test
    @DisplayName("동일 outboxId는 dedupe되어 한 번만 큐에 들어간다")
    fun dedupeByOutboxIdTest() {
        queueChannel.publish(MailQueueMessage(1L))
        queueChannel.publish(MailQueueMessage(1L))
        queueChannel.publish(MailQueueMessage(2L))

        val first = queueChannel.take()
        val second = queueChannel.take()

        assertThat(first.outboxId).isEqualTo(1L)
        assertThat(second.outboxId).isEqualTo(2L)
    }
}
