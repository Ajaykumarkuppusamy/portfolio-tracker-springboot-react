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
                .map(symbol -> SymbolDto.builder()
                        .id(symbol.getId())
                        .ticker(symbol.getTicker())
                        .name(symbol.getName())
                        .exchange(symbol.getExchange())
                        .currency(symbol.getCurrency())
                        .build())
                .collect(Collectors.toList());
    }
}
