package com.example.portfoliotracker.service;

import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Primary
public class YahooFinanceQuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceQuoteServiceImpl.class);
    private final RestTemplate restTemplate;

    public YahooFinanceQuoteServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            // Establishing CookieManager globally can still help with overall Yahoo stability
            if (java.net.CookieHandler.getDefault() == null) {
                java.net.CookieManager cookieManager = new java.net.CookieManager();
                cookieManager.setCookiePolicy(java.net.CookiePolicy.ACCEPT_ALL);
                java.net.CookieHandler.setDefault(cookieManager);
            }
            // Set Global User-Agent for all requests
            System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        } catch (Exception e) {
            logger.warn("Failed to initialize session headers: {}", e.getMessage());
        }
    }

    @Override
    public Optional<PriceTick> getLatestPrice(Symbol symbol) {
        return Optional.ofNullable(getLatestPrices(java.util.Collections.singletonList(symbol)).get(symbol.getId()));
    }

    @Override
    public java.util.Map<Long, PriceTick> getLatestPrices(java.util.List<Symbol> symbols) {
        if (symbols == null || symbols.isEmpty()) return java.util.Collections.emptyMap();
        
        java.util.Map<Long, PriceTick> results = new java.util.HashMap<>();

        for (Symbol symbol : symbols) {
            String ticker = normalizeTicker(symbol.getTicker());
            // The v8 Chart endpoint is currently the most stable "free" gateway that doesn't 401
            String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d", ticker);

            try {
                logger.info("Fetching price from Yahoo v8 Bridge for: {}", ticker);
                JsonNode root = restTemplate.getForObject(url, JsonNode.class);

                if (root != null && root.has("chart") && root.get("chart").has("result") && !root.get("chart").get("result").isNull()) {
                    JsonNode meta = root.get("chart").get("result").get(0).get("meta");

                    BigDecimal lastPrice = new BigDecimal(meta.get("regularMarketPrice").asText());
                    BigDecimal prevClose = meta.has("chartPreviousClose") 
                        ? new BigDecimal(meta.get("chartPreviousClose").asText()) 
                        : lastPrice;
                    
                    PriceTick tick = PriceTick.builder()
                            .symbol(symbol)
                            .asOf(LocalDateTime.now())
                            .last(lastPrice)
                            .prevClose(prevClose)
                            .build();
                    
                    results.put(symbol.getId(), tick);
                } else {
                    logger.warn("Received empty v8 chart response for ticker: {}", ticker);
                }
            } catch (Exception e) {
                logger.error("Failed to fetch price via bridge for {}: {}", ticker, e.getMessage());
            }
        }
        return results;
    }

    private String normalizeTicker(String ticker) {
        if (ticker == null) return null;
        // Automatically qualify common Indian market tickers
        if (!ticker.contains(".") && (ticker.equals("TCS") || ticker.equals("HDFCBANK") || ticker.equals("RELIANCE"))) {
            return ticker + ".NS";
        }
        return ticker;
    }
}
