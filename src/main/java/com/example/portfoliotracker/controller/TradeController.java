package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.entity.Portfolio;
import com.example.portfoliotracker.entity.Symbol;
import com.example.portfoliotracker.entity.Trade;
import com.example.portfoliotracker.repository.PortfolioRepository;
import com.example.portfoliotracker.repository.SymbolRepository;
import com.example.portfoliotracker.repository.TradeRepository;
import com.example.portfoliotracker.service.PortfolioCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeRepository tradeRepository;
    private final PortfolioRepository portfolioRepository;
    private final SymbolRepository symbolRepository;
    private final PortfolioCalculatorService portfolioCalculatorService;

    public TradeController(TradeRepository tradeRepository, PortfolioRepository portfolioRepository, SymbolRepository symbolRepository, PortfolioCalculatorService portfolioCalculatorService) {
        this.tradeRepository = tradeRepository;
        this.portfolioRepository = portfolioRepository;
        this.symbolRepository = symbolRepository;
        this.portfolioCalculatorService = portfolioCalculatorService;
    }

    @PostMapping
    public ResponseEntity<Trade> addTrade(@RequestBody Trade trade) {
        // In a real app, you'd look up portfolio and symbol from IDs in a DTO
        Portfolio portfolio = portfolioRepository.findById(trade.getPortfolio().getId()).orElseThrow();
        Symbol symbol = symbolRepository.findById(trade.getSymbol().getId()).orElseThrow();
        trade.setPortfolio(portfolio);
        trade.setSymbol(symbol);
        
        Trade savedTrade = tradeRepository.save(trade);
        
        if(trade.getSide() == Trade.TradeSide.SELL) {
            portfolioCalculatorService.processLotsForSellTrade(savedTrade);
        }
        
        return ResponseEntity.ok(savedTrade);
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getTrades(@RequestParam Long portfolioId) {
        return ResponseEntity.ok(tradeRepository.findByPortfolioIdOrderByTradeDatetimeAsc(portfolioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        // Deleting trades can complicate P&L calculations.
        // A soft delete or adjustment logic would be better in a real system.
        tradeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
