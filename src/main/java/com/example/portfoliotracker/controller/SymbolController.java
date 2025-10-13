package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.SymbolDto;
import com.example.portfoliotracker.service.SymbolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
