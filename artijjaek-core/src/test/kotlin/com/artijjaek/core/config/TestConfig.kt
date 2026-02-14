package com.artijjaek.core.config

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = ["com.artijjaek.core"])
@EntityScan(basePackages = ["com.artijjaek.core"])
@Import(QueryDslConfig::class)
class TestConfig
