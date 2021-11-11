package com.testassignment.quotacounts

import org.springframework.stereotype.Service

@Service
class QuotesService(
    private val quoteRepository: QuoteRepository,
    private val energyLevelRepository: EnergyLevelRepository
) {

    fun saveQuotes(quotes: List<Quote>) {
        quotes.forEach {
            validateQuote(it)
        }

        quoteRepository.saveAll(quotes)
    }

    private fun validateQuote(quote: Quote) {
        if (quote.ask <= quote.bid!!) {
            throw IllegalArgumentException("bid value should be less than ask for quote: ${quote.id}")
        }
    }

    fun findEnergyLevels(): List<EnergyLevel> {
        return energyLevelRepository.findAll()
    }

    fun calculateElvl(isin: String): Double? {
        val quotes = quoteRepository.findAllByIsin(isin)
        if (quotes.isEmpty()) {
            return null
        }
        var energyLevel = energyLevelRepository.findByIsin(isin)
        if (energyLevel == null) {
            energyLevel = initEnergyLevelForQuote(isin)
        }

        quotes.forEach{
            if (energyLevel.bestPrice == null) {
                energyLevel.bestPrice = if (it.bid != null) it.bid else it.ask
            } else {
                if (it.bid!! > energyLevel.bestPrice!!) {
                    energyLevel.bestPrice = it.bid
                } else if (it.ask < energyLevel.bestPrice!!) {
                    energyLevel.bestPrice = it.ask
                }
            }
        }

        energyLevelRepository.save(energyLevel!!)

        return energyLevel.bestPrice
    }

    private fun initEnergyLevelForQuote(isin: String): EnergyLevel {
        return EnergyLevel(isin = isin, bestPrice = null)
    }
}
