package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.AlphaVantageQuoteResponse;
import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class LiveQuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(LiveQuoteServiceImpl.class);
    private final RestTemplate restTemplate;

    @Value("${alpha.vantage.api.url}")
    private String apiUrl;

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    public LiveQuoteServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<PriceTick> getLatestPrice(Symbol symbol) {
        String apiSymbol = symbol.getTicker() + ".BSE";
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", apiUrl, apiSymbol, apiKey);

        try {
            logger.info("Fetching live price for symbol: {}", apiSymbol);
            AlphaVantageQuoteResponse response = restTemplate.getForObject(url, AlphaVantageQuoteResponse.class);

            if (response != null && response.getGlobalQuote() != null && response.getGlobalQuote().getPrice() != null) {
                AlphaVantageQuoteResponse.GlobalQuote quote = response.getGlobalQuote();
                return Optional.of(PriceTick.builder()
                        .symbol(symbol)
                        .asOf(LocalDateTime.now())
                        .last(quote.getPrice())
                        .prevClose(quote.getPreviousClose())
                        .build());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch live price for symbol " + apiSymbol, e);
        }
        return Optional.empty();
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
