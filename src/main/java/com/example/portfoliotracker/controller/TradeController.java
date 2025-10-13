package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.TradeDto;
import com.example.portfoliotracker.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<TradeDto> addTrade(@RequestParam Long portfolioId, 
                                             @RequestParam Long symbolId, // --- THIS IS THE FIX ---
                                             @RequestBody TradeDto tradeDto) {
        // Pass all three arguments to the service
        TradeDto createdTrade = tradeService.createTrade(tradeDto, portfolioId, symbolId);
        return ResponseEntity.ok(createdTrade);
    }

    @GetMapping
    public ResponseEntity<List<TradeDto>> getTrades(@RequestParam Long portfolioId) {
        List<TradeDto> trades = tradeService.getTradesForPortfolio(portfolioId);
        return ResponseEntity.ok(trades);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable("id") Long tradeId) {
        tradeService.deleteTrade(tradeId);
        return ResponseEntity.noContent().build();
    }
}

