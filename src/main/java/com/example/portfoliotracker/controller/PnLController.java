package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.RealizedPnLDto;
import com.example.portfoliotracker.service.PortfolioCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pnl")
public class PnLController {

    private final PortfolioCalculatorService portfolioCalculatorService;

    public PnLController(PortfolioCalculatorService portfolioCalculatorService) {
        this.portfolioCalculatorService = portfolioCalculatorService;
    }

    @GetMapping("/realized")
    public ResponseEntity<List<RealizedPnLDto>> getRealizedPnL(@RequestParam Long portfolioId) {
        return ResponseEntity.ok(portfolioCalculatorService.calculateRealizedPnL(portfolioId));
    }
}
