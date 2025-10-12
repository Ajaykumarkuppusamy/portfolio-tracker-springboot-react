package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.entity.Cashflow;
import com.example.portfoliotracker.repository.CashflowRepository;
import com.example.portfoliotracker.repository.PortfolioRepository;
import com.example.portfoliotracker.repository.SymbolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashflows")
public class CashflowController {

    private final CashflowRepository cashflowRepository;
    private final PortfolioRepository portfolioRepository;
    private final SymbolRepository symbolRepository;


    public CashflowController(CashflowRepository cashflowRepository, PortfolioRepository portfolioRepository, SymbolRepository symbolRepository) {
        this.cashflowRepository = cashflowRepository;
        this.portfolioRepository = portfolioRepository;
        this.symbolRepository = symbolRepository;
    }

    @PostMapping
    public ResponseEntity<Cashflow> addCashflow(@RequestBody Cashflow cashflow) {
        // Again, proper DTOs and lookups are needed for a robust implementation
        portfolioRepository.findById(cashflow.getPortfolio().getId()).orElseThrow();
        if (cashflow.getSymbol() != null && cashflow.getSymbol().getId() != null) {
            symbolRepository.findById(cashflow.getSymbol().getId()).orElseThrow();
        }
        return ResponseEntity.ok(cashflowRepository.save(cashflow));
    }

    @GetMapping
    public ResponseEntity<List<Cashflow>> getCashflows(@RequestParam Long portfolioId) {
        return ResponseEntity.ok(cashflowRepository.findByPortfolioId(portfolioId));
    }
}
