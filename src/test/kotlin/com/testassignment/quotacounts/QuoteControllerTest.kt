package com.testassignment.quotacounts

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class QuoteControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper
) {
    @MockkBean
    private lateinit var quotesService: QuotesService

    @Test
    fun `Save Quotes`() {
        val quote1 = Quote(isin = "RU000A0JX0J2", bid = 100.2, ask = 101.9)
        val quote2 = Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9)

        val quotes = listOf(quote1, quote2)

        every { quotesService.saveQuotes(any()) } returns Unit

        mockMvc.perform(
            post("/api/v1/quote-service/quotes")
                .content(mapper.writeValueAsString(quotes))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `Get Bulk all Energy Levels`() {
        val elvl1 = EnergyLevel(isin = "RU000A0JX0J1", bestPrice = 100.0)
        val elvl2 = EnergyLevel(isin = "RU000A0JX0J2", bestPrice = 0.0)
        val elvl3 = EnergyLevel(isin = "RU000A0JX0J3", bestPrice = 105.0)
        every { quotesService.findEnergyLevels() } returns listOf(elvl1, elvl2, elvl3)

        mockMvc.perform(get("/api/v1/quote-service/energy-level/bulk").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].isin").value(elvl1.isin))
            .andExpect(jsonPath("$[1].isin").value(elvl2.isin))
            .andExpect(jsonPath("$[2].isin").value(elvl3.isin))
    }

    @Test
    fun `Get Bulk all Energy Levels When no entities found`() {
        every { quotesService.findEnergyLevels() } returns listOf()
        mockMvc.perform(get("/api/v1/quote-service/energy-level/bulk").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("[]"))
    }

    @Test
    fun `Get Elvl by ISIN contains elvl value`() {
        val isin = "RU000A0JX0J2"
        val elvl = 100.0
        every { quotesService.calculateElvl(isin) } returns elvl

        mockMvc.perform(get("/api/v1/quote-service/energy-level/get-by-isin/${isin}").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(elvl.toString()))
    }
}
