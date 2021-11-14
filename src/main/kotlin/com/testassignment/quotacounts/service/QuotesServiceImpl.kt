package com.testassignment.quotacounts.service

import com.testassignment.quotacounts.model.EnergyLevel
import com.testassignment.quotacounts.model.Quote
import com.testassignment.quotacounts.repository.EnergyLevelRepository
import com.testassignment.quotacounts.repository.QuoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuotesServiceImpl(
    private val quoteRepository: QuoteRepository,
    private val energyLevelRepository: EnergyLevelRepository
) : QuoteService {

    override fun addQuotes(quotes: List<Quote>) {
        quotes.parallelStream().forEach {
            validateQuote(it)
        }

        quotes.forEach {
            calculateElvlByQuote(it)
        }

        quoteRepository.saveAll(quotes)
    }

    private fun validateQuote(quote: Quote) {
        if (quote.ask <= quote.bid!!) {
            throw IllegalArgumentException("bid value should be less than ask for quote: ${quote.id}")
        }
    }

    override fun findEnergyLevels(): List<EnergyLevel> {
        return energyLevelRepository.findAll()
    }

    override fun getElvlByIsin(isin: String): EnergyLevel? {
        return energyLevelRepository.findByIsin(isin)
    }

    @Transactional
    fun calculateElvlByQuote(quote: Quote): Double? {
        var energyLevel = resolveEnergyLevel(quote.isin)

        if (energyLevel.bestPrice == null) {
            energyLevel.bestPrice = if (quote.bid != null) quote.bid else quote.ask
        } else {
            if (quote.bid != null && quote.bid!! > energyLevel.bestPrice!!) {
                energyLevel.bestPrice = quote.bid
            } else if (quote.ask < energyLevel.bestPrice!!) {
                energyLevel.bestPrice = quote.ask
            }
        }

        saveEnergyLevel(energyLevel)

        return energyLevel.bestPrice
    }

    fun saveEnergyLevel(energyLevel: EnergyLevel) {
        energyLevelRepository.save(energyLevel!!)
    }

    fun resolveEnergyLevel(isin: String): EnergyLevel {
        var energyLevel = energyLevelRepository.findByIsin(isin)
        if (energyLevel == null) {
            energyLevel = initEnergyLevelForQuote(isin)
        }

        return energyLevel
    }

    private fun initEnergyLevelForQuote(isin: String): EnergyLevel {
        return EnergyLevel(isin = isin, bestPrice = null)
    }
}
