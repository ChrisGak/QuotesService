package com.testassignment.quotacounts

import com.testassignment.quotacounts.model.EnergyLevel
import com.testassignment.quotacounts.model.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import kotlin.system.measureTimeMillis

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IntegrationTests(
    @Autowired
    val restTemplate: TestRestTemplate
) {

    private val SERVICE_PATH = "/api/v1/quote-service"

    @BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @Order(0)
    @Test
    fun `Assert main page title, content and status code`() {
        println(">> Assert main page title, content and status code")
        val entity = restTemplate.getForEntity<String>("/")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("<h1>Quote</h1>")
    }

    /**
     * Test getElvlByIsin before 'add quotes' tests on repository initial configuration
     */
    @Order(1)
    @Test
    fun `Assert get elvl by ISIN response contains energy level value as null if no records found`() {
        println(">> Assert get elvl by ISIN response contains energy level value as null if no records found")
        val isin = "RU000A0JX0J0" // See com.testassignment.quotacounts.QuoteConfiguration.databaseInitializer
        val entity = restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Order(2)
    @Test
    fun `Assert get elvl by ISIN response contains energy level value if record found`() {
        println(">> Assert get elvl by ISIN response contains energy level value - case 1 elvl is 100,2")
        val isin = "RU000A0JX0J1"
        val expectedElvl = 100.2 // See com.testassignment.quotacounts.QuoteConfiguration.databaseInitializer
        val entity = restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.bestPrice).isEqualTo(expectedElvl)
    }

    /**
     * Test getElvlByIsin before 'add quotes' tests on repository initial configuration
     */
    @Order(3)
    @Test
    fun `Assert get all elvl response contains all found energy levels`() {
        println(">> Assert get all elvl response contains all found energy levels")
        val entities = restTemplate.getForEntity<List<EnergyLevel>>(SERVICE_PATH + "/energy-level/bulk")
        assertThat(entities.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entities.body?.size).isEqualTo(2)
    }

    /**
     * Test addQuotes
     */
    @Order(4)
    @Test
    fun `Assert add quotes performs validation and calculates elvl`() {
        println(">> Assert add quotes performs validation and calculates elvl")
        val isin = "RU000A0JX0J2"
        val quote1 = Quote(isin = isin, bid = 100.2, ask = 101.9)
        val quote2 = Quote(isin = isin, bid = 100.5, ask = 101.9)
        val quotes = listOf(quote1, quote2)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)

        val energyLevelEntity =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(energyLevelEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity.body?.bestPrice).isEqualTo(100.5)
    }

    @Order(5)
    @Test
    fun `Assert add quotes performs validation and calculates elvl for 100 quotes when no elvl exists`() {
        println(">> Assert add quotes performs validation and calculates elvl for 100 quotes")
        val isin = "RU000A0JX0J0"
        val expectedElvl = 100.2
        val quotesRequest = mutableListOf<Quote>()
        for (i in 1..100) {
            quotesRequest.add(Quote(isin = isin, bid = 100.2, ask = 101.9))
        }

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotesRequest)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)

        val energyLevelEntity =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(energyLevelEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity.body?.bestPrice).isEqualTo(expectedElvl)
    }

    @Order(6)
    @Test
    fun `Assert add quotes performs validation when isin length less 12 - isin length 12 symbols`() {
        println(">> Assert add quotes performs validation")
        val longIsinQuote = Quote(isin = "RU000", bid = 100.2, ask = 101.9)
        val quotes = listOf(longIsinQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Order(7)
    @Test
    fun `Assert add quotes performs validation when isin length more 12 - isin length 12 symbols`() {
        println(">> Assert add quotes performs validation")
        val longIsinQuote = Quote(isin = "RU000A0JX0J2RU000A0JX0J2RU000A0JX0J2RU000A0JX0J2", bid = 100.2, ask = 101.9)
        val quotes = listOf(longIsinQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Order(8)
    @Test
    fun `Assert add quotes performs validation - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 150.0, ask = 100.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Order(9)
    @Test
    fun `Assert add quotes performs validation when bin equals ask - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 100.0, ask = 100.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Order(10)
    @Test
    fun `Assert add quotes performs validation when bin less ask - bin must be less than ask`() {
        val askLessBidQuote = Quote(isin = "RU000A0JX0J2", bid = 100.0, ask = 101.0)
        val quotes = listOf(askLessBidQuote)

        val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotes)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Order(11)
    @Test
    fun `Assert add quotes performs validation and calculates elvl for 100 quotes`() {
        println(">> Assert add quotes performs validation and calculates elvl for 100 quotes")
        val isin =
            "RU000A0JX0J3" // Energy Level already exists, see com.testassignment.quotacounts.QuoteConfiguration.databaseInitializer
        val expectedElvl = 101.9 // Case If ask < elvl, then elvl = ask
        val quotesRequest = mutableListOf<Quote>()
        for (i in 1..100) {
            quotesRequest.add(Quote(isin = isin, bid = 100.2, ask = 101.9))
        }

        val timeInMillis = measureTimeMillis {
            val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotesRequest)
            assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        }

        val energyLevelEntity =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(energyLevelEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity.body?.bestPrice).isEqualTo(expectedElvl)

        println(">> timeInMillis: ${timeInMillis}")
        assertThat(timeInMillis).isLessThanOrEqualTo(1000)
    }

    @Order(12)
    @Test
    fun `Assert add quotes performs validation and calculates elvl for 1000 quotes`() {
        println(">> Assert add quotes performs validation and calculates elvl for 1000 quotes")
        val isin =
            "RU000A0JX0J4"
        val expectedElvl = 200.0 // Case If bid > elvl, then elvl = bid
        val quotesRequest = mutableListOf<Quote>()
        for (i in 1..1000) {
            quotesRequest.add(Quote(isin = isin, bid = 200.0, ask = 300.0))
        }

        val timeInMillis = measureTimeMillis {
            val entity = restTemplate.postForEntity<Any>(SERVICE_PATH + "/quotes", quotesRequest)
            assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        }
        println(">> timeInMillis: ${timeInMillis}")

        val energyLevelEntity =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${isin}")
        assertThat(energyLevelEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity.body?.bestPrice).isEqualTo(expectedElvl)
    }

    @Order(13)
    @Test
    fun `Assert add quotes performs validation and calculates elvl for 100 requests - 3 isin`() {
        println(">> Assert add quotes performs validation and calculates elvl for 100 requests - 3 isin")
        val quote1 = Quote(isin = "RU000A0JX0J6", bid = 100.0, ask = 110.0)
        val quote2 = Quote(isin = "RU000A0JX0J7", bid = 200.0, ask = 210.0)
        val quote3 = Quote(isin = "RU000A0JX0J8", bid = 300.0, ask = 310.0)
        val quote4 = Quote(isin = "RU000A0JX0J9", bid = 400.0, ask = 410.0)

        val quotesRequest = mutableListOf<Quote>()
        for (i in 1..25) {
            quotesRequest.add(quote1)
            quotesRequest.add(quote2)
            quotesRequest.add(quote3)
            quotesRequest.add(quote4)
        }

        val timeInMillis = measureTimeMillis {
            val entity = restTemplate.postForEntity<Any>(
                SERVICE_PATH + "/quotes",
                quotesRequest
            )
            assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        }

        val energyLevelEntity1 =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${quote1.isin}")
        assertThat(energyLevelEntity1.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity1.body?.bestPrice).isEqualTo(quote1.bid)

        val energyLevelEntity2 =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${quote2.isin}")
        assertThat(energyLevelEntity2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity2.body?.bestPrice).isEqualTo(quote2.bid)

        val energyLevelEntity3 =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${quote3.isin}")
        assertThat(energyLevelEntity3.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity3.body?.bestPrice).isEqualTo(quote3.bid)

        val energyLevelEntity4 =
            restTemplate.getForEntity<EnergyLevel>(SERVICE_PATH + "/energy-level/get-by-isin/${quote4.isin}")
        assertThat(energyLevelEntity4.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(energyLevelEntity4.body?.bestPrice).isEqualTo(quote4.bid)

        println(">> timeInMillis: ${timeInMillis}")
        assertThat(timeInMillis).isLessThanOrEqualTo(1000)
    }

    @AfterAll
    fun tearDown() {
        println(">> Tear down")
    }
}


