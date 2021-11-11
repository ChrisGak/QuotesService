package com.testassignment.quotacounts

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuotaCountsApplication

fun main(args: Array<String>) {
    runApplication<QuotaCountsApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
