package com.testassignment.quotacounts

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController {

    @GetMapping("/")
    fun begin(model: Model): String {
        model["title"] = "Quote"
        return "main"
    }
}
