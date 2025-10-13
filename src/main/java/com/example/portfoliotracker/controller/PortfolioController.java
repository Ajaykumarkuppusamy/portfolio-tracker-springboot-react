package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.PortfolioDto;
import com.example.portfoliotracker.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getPortfolios(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PortfolioDto> portfolios = portfolioService.getPortfoliosForUser(principal.getName());
        return ResponseEntity.ok(portfolios);
    }

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestBody PortfolioDto portfolioDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // The entire creation logic is now handled by the service in a single transaction
            PortfolioDto createdPortfolio = portfolioService.createPortfolio(portfolioDto, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

