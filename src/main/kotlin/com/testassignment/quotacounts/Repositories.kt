package com.testassignment.quotacounts

import org.springframework.data.repository.CrudRepository

interface QuoteRepository : CrudRepository<Quote, Long> {
    fun findAllByIsin(isin: String): List<Quote>
}

interface EnergyLevelRepository : CrudRepository<EnergyLevel, Long> {
    override fun findAll(): List<EnergyLevel>

    fun findByIsin(isin: String): EnergyLevel?
}
