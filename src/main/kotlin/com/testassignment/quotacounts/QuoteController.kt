package com.testassignment.quotacounts

import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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

    /**
     * Сервис получает котировки
     * Сервис хранит историю полученных котировок в БД
     */
    @PostMapping("/quotes")
    fun addQuotes(@RequestBody quotes: List<@Valid Quote>, bindingResult: BindingResult) {
        if (!bindingResult.hasErrors()) {
            quotesService.saveQuotes(quotes)
        }
    }

    /**
     * Сервис рассчитывает elvl (правила ниже)
     * Сервис предоставляет elvl по isin
     */
    @GetMapping("/energy-level/get-by-isin/{isin}")
    fun getElvlByIsin(@PathVariable isin: String, model: Model): Double? {
        return quotesService.calculateElvl(isin)
    }

    /**
     * Сервис предоставляет перечень всех elvls
     */
    @GetMapping("/energy-level/bulk")
    fun findAll(): List<EnergyLevel> {
        return quotesService.findEnergyLevels()
    }
}
