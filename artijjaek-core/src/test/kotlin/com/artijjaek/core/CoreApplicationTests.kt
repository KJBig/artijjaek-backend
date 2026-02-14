package com.artijjaek.core

import com.artijjaek.core.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfig::class])
class CoreApplicationTests {

    @Test
    fun contextLoads() {
    }

}
