package com.noati.core.repository

import com.noati.core.domain.Subscribe
import org.springframework.data.jpa.repository.JpaRepository

interface SubscribeRepository : JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {

}