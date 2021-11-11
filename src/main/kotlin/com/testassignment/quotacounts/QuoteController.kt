package com.testassignment.quotacounts

import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/api/v1/quote-service")
class QuoteController(
    private val quotesService: QuotesService
) {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onIllegalArgumentException(e: IllegalArgumentException?) {
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onConstraintViolationException(e: ConstraintViolationException?) {
    }

    @PostMapping("/quotes")
    fun addQuotes(@RequestBody quotes: List<@Valid Quote>, bindingResult: BindingResult) {
        if(!bindingResult.hasErrors()) {
            quotesService.saveQuotes(quotes)
        }
    }

    @GetMapping("/energy-level/get-by-isin/{isin}")
    fun getElvlByIsin(@PathVariable isin: String, model: Model): Double? {
        return quotesService.calculateElvl(isin)
    }

    @GetMapping("/energy-level/bulk")
    fun findAll(): List<EnergyLevel> {
        return quotesService.findEnergyLevels()
    }
}
