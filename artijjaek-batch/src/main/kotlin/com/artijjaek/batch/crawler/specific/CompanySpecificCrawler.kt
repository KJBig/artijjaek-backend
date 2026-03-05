package com.artijjaek.batch.crawler.specific

import com.artijjaek.batch.crawler.Crawler

interface CompanySpecificCrawler : Crawler {
    val companyNameEn: String
}