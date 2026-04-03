package com.example.portfoliotracker.service;

import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class DummyQuoteServiceImpl implements QuoteService {

    private final Random random = new Random();

    @Override
    public Optional<PriceTick> getLatestPrice(Symbol symbol) {
        BigDecimal lastPrice = BigDecimal.valueOf(100 + random.nextDouble() * 900).setScale(2, RoundingMode.HALF_UP);
        BigDecimal prevClose = lastPrice.multiply(BigDecimal.valueOf(0.95 + random.nextDouble() * 0.1)).setScale(2, RoundingMode.HALF_UP);
        
        return Optional.of(PriceTick.builder()
                .symbol(symbol)
                .asOf(LocalDateTime.now())
                .last(lastPrice)
                .prevClose(prevClose)
                .build());
    }

    @Override
    public Map<Long, PriceTick> getLatestPrices(List<Symbol> symbols) {
        Map<Long, PriceTick> results = new HashMap<>();
        for (Symbol symbol : symbols) {
            getLatestPrice(symbol).ifPresent(tick -> results.put(symbol.getId(), tick));
        }
        return results;
    }
}