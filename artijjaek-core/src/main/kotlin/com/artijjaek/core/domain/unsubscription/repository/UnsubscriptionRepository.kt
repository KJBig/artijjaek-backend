package com.artijjaek.core.domain.unsubscription.repository

import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
import org.springframework.data.jpa.repository.JpaRepository

interface UnsubscriptionRepository : JpaRepository<Unsubscription, Long> {
}