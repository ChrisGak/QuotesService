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
        quoteRepository.save(Quote(isin = "RU000A0JX0J1", bid = 100.2, ask = 101.9))
        quoteRepository.save(Quote(isin = "RU000A0JX0J2", bid = 100.2, ask = 101.9))
        quoteRepository.save(Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9))
        energyLevelRepository.save(EnergyLevel(isin = "RU000A0JX0J1", bestPrice = 100.0))
        energyLevelRepository.save(EnergyLevel(isin = "RU000A0JX0J3", bestPrice = 105.0))
    }
}
