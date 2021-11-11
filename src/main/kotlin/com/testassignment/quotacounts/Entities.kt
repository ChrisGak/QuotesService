package com.testassignment.quotacounts

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Size

@Entity
class Quote(
    @get:Size(min=12, max=12) // added annotation use-site target here
    val isin: String,
    var bid: Double? = null,
    var ask: Double,
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class EnergyLevel(
    @get:Size(min=12, max=12) // added annotation use-site target here
    val isin: String,
    var bestPrice: Double? = null,
    @Id @GeneratedValue var id: Long? = null
)
