package com.noati.core.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.batch.BatchDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Value("\${spring.datasource.url}")
    private val url: String? = null

    @Value("\${spring.datasource.username}")
    private val username: String? = null

    @Value("\${spring.datasource.password}")
    private val password: String? = null

    @Value("\${spring.datasource.driver-class-name}")
    private val driverClassName: String? = null

    @Value("\${spring.batch-datasource.url}")
    private val batchUrl: String? = null

    @Value("\${spring.batch-datasource.username}")
    private val batchUsername: String? = null

    @Value("\${spring.batch-datasource.password}")
    private val batchPassword: String? = null

    @Value("\${spring.batch-datasource.driver-class-name}")
    private val batchDriverClassName: String? = null

    @Bean
    @Primary
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build()
    }

    @Bean
    @BatchDataSource
    fun batchDataSource(): DataSource? {
        return DataSourceBuilder.create()
            .url(batchUrl)
            .username(batchUsername)
            .password(batchPassword)
            .driverClassName(batchDriverClassName)
            .build()
    }

    @Bean
    fun jpaTransactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val jpaTransactionManager = JpaTransactionManager(entityManagerFactory)
        jpaTransactionManager.dataSource = dataSource()
        return jpaTransactionManager
    }
}