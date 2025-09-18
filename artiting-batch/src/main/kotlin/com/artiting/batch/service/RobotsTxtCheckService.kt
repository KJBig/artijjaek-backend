package com.artiting.batch.service

import com.artiting.core.domain.Company
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URL

@Service
class RobotsTxtCheckService {

    fun isCrawlAllowed(company: Company): Boolean {
        val robotsTxtContent = try {
            URL("${company.baseUrl}/robots.txt").readText()
        } catch (e: IOException) {
            return true
        }

        val disallowPaths = robotsTxtContent
            .lines()
            .map { it.trim() }
            .filter { it.startsWith("Disallow:", ignoreCase = true) }
            .map { it.substringAfter(":").trim() }

        return when (company.crawlUrl) {
            "/" -> !disallowPaths.contains("/")
            else -> disallowPaths.filter { it != "/" }.none { it.isNotEmpty() && company.crawlUrl.startsWith(it) }
        }
    }
}