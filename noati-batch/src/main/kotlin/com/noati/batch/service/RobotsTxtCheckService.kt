package com.noati.batch.service

import com.noati.core.domain.Company
import org.springframework.stereotype.Service
import java.net.URL

@Service
class RobotsTxtCheckService {

    fun isCrawlAllowed(company: Company): Boolean {
        val disallowPaths = URL("${company.baseUrl}/robots.txt").readText()
            .lines()
            .filter { it.contains("Disallow") }
            .map { it.replace(" ", "").replace("Disallow:", "") }
        return when (company.crawlUrl) {
            "/" -> !disallowPaths.contains("/")
            else -> disallowPaths.none { it.contains(company.crawlUrl.split("/")[1]) }
        }
    }
}