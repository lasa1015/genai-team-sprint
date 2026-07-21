package com.fx.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/convert")
    public String convertPage() {
        return "forward:/convert.html";
    }

    @GetMapping("/history")
    public String historyPage() {
        return "forward:/history.html";
    }
}