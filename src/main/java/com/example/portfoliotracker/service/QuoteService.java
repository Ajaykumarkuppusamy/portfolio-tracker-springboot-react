package com.example.portfoliotracker.service;

import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuoteService {
    Optional<PriceTick> getLatestPrice(Symbol symbol);
    Map<Long, PriceTick> getLatestPrices(List<Symbol> symbols);
}
