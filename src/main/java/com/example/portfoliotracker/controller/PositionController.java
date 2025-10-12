package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.PositionDto;
import com.example.portfoliotracker.service.PortfolioCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PortfolioCalculatorService portfolioCalculatorService;

    public PositionController(PortfolioCalculatorService portfolioCalculatorService) {
        this.portfolioCalculatorService = portfolioCalculatorService;
    }

    @GetMapping
    public ResponseEntity<List<PositionDto>> getPositions(@RequestParam Long portfolioId) {
        List<PositionDto> positions = portfolioCalculatorService.calculatePositions(portfolioId);
        return ResponseEntity.ok(positions);
    }
}
