package com.noati.batch.config

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class BatchConfig(
    @Qualifier("batchDataSource") private val batchDataSource: DataSource,
    @Qualifier("jpaTransactionManager") private val transactionManager: PlatformTransactionManager
) : DefaultBatchConfiguration() {

    override fun getDataSource(): DataSource = batchDataSource

    override fun getTransactionManager(): PlatformTransactionManager = transactionManager

    @Bean
    fun dataSourceInitializer(
        @Qualifier("batchDataSource") batchDataSource: DataSource,
        @Value("\${spring.batch.jdbc.initialize-schema}") initializeSchema: String
    ): DataSourceInitializer {
        val initializer = DataSourceInitializer()
        initializer.setDataSource(batchDataSource)
        initializer.setEnabled(initializeSchema == "always" || initializeSchema == "embedded")
        val populator = ResourceDatabasePopulator()
        // always일 때 기존 테이블 드롭
        if (initializeSchema == "always") {
            populator.addScript(ClassPathResource("org/springframework/batch/core/schema-drop-mysql.sql"))
        }
        // 테이블 생성
        populator.addScript(ClassPathResource("org/springframework/batch/core/schema-mysql.sql"))
        initializer.setDatabasePopulator(populator)
        return initializer
    }

}