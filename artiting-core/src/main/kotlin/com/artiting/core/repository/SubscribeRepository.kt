package com.artiting.core.repository

import com.artiting.core.domain.Subscribe
import org.springframework.data.jpa.repository.JpaRepository

interface SubscribeRepository : JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {

}