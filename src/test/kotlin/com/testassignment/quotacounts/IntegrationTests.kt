package com.testassignment.quotacounts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    private val SERVICE_PATH = "/api/v1/quote-service"

    @BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @Test
    fun `Assert main page title, content and status code`() {
        println(">> Assert main page title, content and status code")
        val entity = restTemplate.getForEntity<String>("/")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("<h1>Quote</h1>")
    }

    @Test
    fun `Assert add quotes`() {
        println(">> Assert add quotes performs validation")
        val quote1 = Quote(isin = "RU000A0JX0J2", bid = 100.2, ask = 101.9)
        val quote2 = Quote(isin = "RU000A0JX0J2", bid = 100.5, ask = 101.9)
        val quotes = listOf(quote1, quote2)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Assert add quotes performs validation when isin length less 12 - isin length 12 symbols`() {
        println(">> Assert add quotes performs validation")
        val longIsinQuote = Quote(isin = "RU000", bid = 100.2, ask = 101.9)
        val quotes = listOf(longIsinQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Assert add quotes performs validation when isin length more 12 - isin length 12 symbols`() {
        println(">> Assert add quotes performs validation")
        val longIsinQuote = Quote(isin = "RU000A0JX0J2RU000A0JX0J2RU000A0JX0J2RU000A0JX0J2", bid = 100.2, ask = 101.9)
        val quotes = listOf(longIsinQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Assert add quotes performs validation - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 150.0, ask = 100.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Assert add quotes performs validation when bin equals ask - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 100.0, ask = 100.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Assert add quotes performs validation when bin less ask - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 100.0, ask = 101.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Assert get elvl by ISIN response contains energy level value - case 1 elvl is 100,2`() {
        println(">> Assert get elvl by ISIN response contains energy level value - case 1 elvl is 100,2")
        val isin = "RU000A0JX0J1"
        val expectedElvl = 100.2 // See com.testassignment.quotacounts.QuoteConfiguration.databaseInitializer
        val entity = restTemplate.getForEntity<Double>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isEqualTo(expectedElvl)
    }

    @Test
    fun `Assert get elvl by ISIN response contains energy level value - case 2 elvl is 100,5 bid`() {
        println(">> Assert get elvl by ISIN response contains energy level value. Case 2 elvl is 100.5 bid")
        val isin = "RU000A0JX0J2"
        val expectedElvl = 100.5 // See com.testassignment.quotacounts.QuoteConfiguration.databaseInitializer
        val entity = restTemplate.getForEntity<Double>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isEqualTo(expectedElvl)
    }

    @AfterAll
    fun tearDown() {
        println(">> Tear down")
    }
}


