package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.PortfolioDto;
import com.example.portfoliotracker.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestBody PortfolioDto portfolioDto, Principal principal) {
        PortfolioDto createdPortfolio = portfolioService.createPortfolio(portfolioDto, principal.getName());
        return new ResponseEntity<>(createdPortfolio, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getUserPortfolios(Principal principal) {
        // Using principal.getName() which returns the username (our email)
        List<PortfolioDto> portfolios = portfolioService.findPortfoliosByUserEmail(principal.getName());
        return ResponseEntity.ok(portfolios);
    }
}
