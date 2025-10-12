package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;
import com.example.portfoliotracker.repository.SymbolRepository;
import com.example.portfoliotracker.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;
    private final SymbolRepository symbolRepository;

    public QuoteController(QuoteService quoteService, SymbolRepository symbolRepository) {
        this.quoteService = quoteService;
        this.symbolRepository = symbolRepository;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<PriceTick> getQuote(@PathVariable String ticker, @RequestParam(defaultValue = "NSE") String exchange) {
        Symbol symbol = symbolRepository.findByTickerAndExchange(ticker, exchange)
                .orElseThrow(() -> new RuntimeException("Symbol not found"));

        return quoteService.getLatestPrice(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
