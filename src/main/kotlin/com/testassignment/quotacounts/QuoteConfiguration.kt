package com.testassignment.quotacounts

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuoteConfiguration {

    @Bean
    fun databaseInitializer(
        quoteRepository: QuoteRepository,
        energyLevelRepository: EnergyLevelRepository
    ) = ApplicationRunner {
        val quote1 = quoteRepository.save(Quote(isin = "RU000A0JX0J1", bid = 100.2, ask = 101.9))
        val quote2 = quoteRepository.save(Quote(isin = "RU000A0JX0J2", bid = 100.2, ask = 101.9))
        val quote3 = quoteRepository.save(Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9))
        val evl1 = energyLevelRepository.save(EnergyLevel(isin = "RU000A0JX0J1", bestPrice = 100.0))
        //val evl2 = energyLevelRepository.save(EnergyLevel(isin = "RU000A0JX0J2"))
        val evl3 = energyLevelRepository.save(EnergyLevel(isin = "RU000A0JX0J3", bestPrice = 105.0))
    }
}
