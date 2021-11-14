package com.testassignment.quotacounts.service

import com.testassignment.quotacounts.model.EnergyLevel
import com.testassignment.quotacounts.model.Quote

interface QuoteService {

    fun addQuotes(quotes: List<Quote>)

    fun getElvlByIsin(isin: String): EnergyLevel?

    fun findEnergyLevels(): List<EnergyLevel>
}
