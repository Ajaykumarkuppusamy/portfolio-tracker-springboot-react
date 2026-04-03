package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.SymbolDto;
import com.example.portfoliotracker.service.SymbolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symbols")
public class SymbolController {

    private final SymbolService symbolService;

    public SymbolController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @GetMapping
    public ResponseEntity<List<SymbolDto>> getAllSymbols() {
        List<SymbolDto> symbols = symbolService.getAllSymbols();
        return ResponseEntity.ok(symbols);
    }

    @PostMapping
    public ResponseEntity<SymbolDto> addSymbol(
            @org.springframework.web.bind.annotation.RequestBody SymbolDto symbolDto) {
        SymbolDto created = symbolService.getOrAddSymbol(symbolDto);
        return ResponseEntity.ok(created);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SymbolController.class);

    @GetMapping("/search")
    public ResponseEntity<String> searchSymbols(@org.springframework.web.bind.annotation.RequestParam("q") String query) {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        String url = "https://query2.finance.yahoo.com/v1/finance/search?q=" + query + "&quotesCount=5&newsCount=0";
        
        try {
            logger.info("Proxying Yahoo search for query: {}", query);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Failed to fetch from Yahoo Finance for query: " + query, e);
            return ResponseEntity.status(500).body("{\"error\": \"Failed to fetch from Yahoo Finance: " + e.getMessage() + "\"}");
        }
    }
}
