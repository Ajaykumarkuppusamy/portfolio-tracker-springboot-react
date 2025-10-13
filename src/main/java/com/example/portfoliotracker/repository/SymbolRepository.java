package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    Optional<Symbol> findByTickerAndExchange(String ticker, String exchange);

    Optional<Symbol> findByTicker(String ticker);
}
