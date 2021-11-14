package com.testassignment.quotacounts

import com.testassignment.quotacounts.model.EnergyLevel
import com.testassignment.quotacounts.model.Quote
import com.testassignment.quotacounts.repository.EnergyLevelRepository
import com.testassignment.quotacounts.repository.QuoteRepository
import com.testassignment.quotacounts.service.QuotesServiceImpl
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteServiceImplTest {
    private var quoteRepository: QuoteRepository = mockk()
    private var energyLevelRepository: EnergyLevelRepository = mockk()
    private var quotesServiceImpl = QuotesServiceImpl(quoteRepository, energyLevelRepository)

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Test
    fun testCalculateElvlByQuote_rule1() {
        val quote = Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9)
        every { energyLevelRepository.findByIsin("RU000A0JX0J2") } returns null
        val elvl = EnergyLevel("RU000A0JX0J2", 100.2)
        every { energyLevelRepository.save(any()) } returns elvl

        val result = quotesServiceImpl.calculateElvlByQuote(quote)
        Assertions.assertThat(result).isEqualTo(100.5) // Так как Bid > elvl , то новый elvl = 100.5 (bid)
    }

    @Test
    fun testCalculateElvlByQuote_rule2() {
        val quote = Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9)
        val elvl = EnergyLevel("RU000A0JX0J2", 102.0)
        every { energyLevelRepository.findByIsin("RU000A0JX0J2") } returns elvl
        every { energyLevelRepository.save(any()) } returns elvl

        val result = quotesServiceImpl.calculateElvlByQuote(quote)
        Assertions.assertThat(result).isEqualTo(101.9) // Если ask < elvl, то elvl = ask
    }

    @Test
    fun testCalculateElvlByQuote_rule3() {
        val quote = Quote(isin = "RU000A0JX0J2", bid = 100.2, ask = 101.9)
        every { energyLevelRepository.findByIsin("RU000A0JX0J2") } returns null
        val mockElvl = mockk<EnergyLevel>()
        every { energyLevelRepository.save(any()) } returns mockElvl

        val result = quotesServiceImpl.calculateElvlByQuote(quote)
        Assertions.assertThat(result).isEqualTo(100.2) // Котировка новая, поэтому elvl - > 100.2
    }

    @Test
    fun testCalculateElvlByQuote_rule4() {
        val quote = Quote(isin = "RU000A0JX0J2", bid = null, ask = 101.9)
        val elvl = EnergyLevel("RU000A0JX0J2", 102.0)
        every { energyLevelRepository.findByIsin("RU000A0JX0J2") } returns elvl
        every { energyLevelRepository.save(any()) } returns elvl

        val result = quotesServiceImpl.calculateElvlByQuote(quote)
        Assertions.assertThat(result).isEqualTo(101.9) // Если bid отсутствует, то elvl = ask
    }
}
