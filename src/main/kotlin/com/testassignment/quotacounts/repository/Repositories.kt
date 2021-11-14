package com.testassignment.quotacounts.repository

import com.testassignment.quotacounts.model.EnergyLevel
import com.testassignment.quotacounts.model.Quote
import org.springframework.data.repository.CrudRepository

interface QuoteRepository : CrudRepository<Quote, Long> {
    fun findAllByIsin(isin: String): List<Quote>
}

interface EnergyLevelRepository : CrudRepository<EnergyLevel, Long> {
    override fun findAll(): List<EnergyLevel>

    fun findByIsin(isin: String): EnergyLevel?
}
