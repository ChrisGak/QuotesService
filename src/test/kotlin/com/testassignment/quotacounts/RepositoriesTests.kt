package com.testassignment.quotacounts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val quoteRepository: QuoteRepository,
    val energyLevelRepository: EnergyLevelRepository
) {

    @Test
    fun `When findByIdOrNull then return Quote`() {
        val quote = Quote("RU000A0JX0J2", 100.2, 101.9)
        entityManager.persist(quote)
        entityManager.flush()
        val found = quoteRepository.findByIdOrNull(quote.id!!)
        assertThat(found).isEqualTo(quote)
    }

    @Test
    fun `When findAllByIsin then return Quotes`() {
        val quote1 = Quote("RU000A0JX0J2", 100.2, 101.9)
        val quote2 = Quote("RU000A0JX0J2", 100.5, 101.9)
        entityManager.persist(quote1)
        entityManager.persist(quote2)
        entityManager.flush()
        val found = quoteRepository.findAllByIsin("RU000A0JX0J2")
        assertThat(found.size).isEqualTo(2)
        assertThat(found.get(0).bid).isEqualTo(100.2)
        assertThat(found.get(1).bid).isEqualTo(100.5)
    }

    @Test
    fun `When findAllByIsin then return empty list if not found`() {
        val found = quoteRepository.findAllByIsin("RU000A0JX0J2")
        assertThat(found.size).isEqualTo(0)
    }

    @Test
    fun `When findByIdOrNull then return EnergyLevel`() {
        val energyLevel = EnergyLevel("RU000A0JX0J2", 100.0)
        entityManager.persist(energyLevel)
        entityManager.flush()
        val found = energyLevelRepository.findByIdOrNull(energyLevel.id!!)
        assertThat(found).isEqualTo(energyLevel)
    }

    @Test
    fun `When findByIsin then return EnergyLevel`() {
        val energyLevel = EnergyLevel("RU000A0JX0J2", 100.0)
        entityManager.persist(energyLevel)
        entityManager.flush()
        val found = energyLevelRepository.findByIsin("RU000A0JX0J2")
        assertThat(found).isEqualTo(energyLevel)
    }

    @Test
    fun `When findByIsin then return null if nothing found`() {
        val found = energyLevelRepository.findByIsin("RU000A0JX0J2")
        assertThat(found).isNull()
    }
}
