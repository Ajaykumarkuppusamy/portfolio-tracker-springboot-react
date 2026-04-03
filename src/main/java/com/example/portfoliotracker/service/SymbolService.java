package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.SymbolDto;
import com.example.portfoliotracker.repository.SymbolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SymbolService {

    private final SymbolRepository symbolRepository;

    public SymbolService(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;
    }

    @Transactional(readOnly = true)
    public List<SymbolDto> getAllSymbols() {
        return symbolRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SymbolDto getOrAddSymbol(SymbolDto symbolDto) {
        return symbolRepository.findByTicker(symbolDto.getTicker())
                .map(this::convertToDto)
                .orElseGet(() -> {
                    com.example.portfoliotracker.entity.Symbol newSymbol = new com.example.portfoliotracker.entity.Symbol();
                    newSymbol.setTicker(symbolDto.getTicker());
                    newSymbol.setName(symbolDto.getName());
                    newSymbol.setExchange(symbolDto.getExchange() != null ? symbolDto.getExchange() : "Unknown");
                    newSymbol.setCurrency(symbolDto.getCurrency() != null ? symbolDto.getCurrency() : "INR");
                    com.example.portfoliotracker.entity.Symbol saved = symbolRepository.save(newSymbol);
                    return convertToDto(saved);
                });
    }

    private SymbolDto convertToDto(com.example.portfoliotracker.entity.Symbol symbol) {
        return SymbolDto.builder()
                .id(symbol.getId())
                .ticker(symbol.getTicker())
                .name(symbol.getName())
                .exchange(symbol.getExchange())
                .currency(symbol.getCurrency())
                .build();
    }
}
