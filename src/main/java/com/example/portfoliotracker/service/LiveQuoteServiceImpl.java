package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.AlphaVantageQuoteResponse;
import com.example.portfoliotracker.entity.PriceTick;
import com.example.portfoliotracker.entity.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Primary // This tells Spring to use this implementation instead of the dummy one
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
        // For Indian stocks on NSE, Alpha Vantage expects the ticker to be suffixed with ".BSE" or ".BOM"
        // We'll default to ".BSE" for this example. This might need adjustment.
        String apiSymbol = symbol.getTicker() + ".BSE";

        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                apiUrl, apiSymbol, apiKey);

        try {
            logger.info("Fetching live price for symbol: {}", apiSymbol);
            AlphaVantageQuoteResponse response = restTemplate.getForObject(url, AlphaVantageQuoteResponse.class);

            if (response != null && response.getGlobalQuote() != null && response.getGlobalQuote().getPrice() != null) {
                AlphaVantageQuoteResponse.GlobalQuote quote = response.getGlobalQuote();

                PriceTick tick = PriceTick.builder()
                        .symbol(symbol)
                        .asOf(LocalDateTime.now())
                        .last(quote.getPrice())
                        .dayOpen(quote.getOpen())
                        .dayHigh(quote.getHigh())
                        .dayLow(quote.getLow())
                        .prevClose(quote.getPreviousClose())
                        .volume(quote.getVolume())
                        .build();
                
                logger.info("Successfully fetched price for {}: {}", apiSymbol, quote.getPrice());
                return Optional.of(tick);
            } else {
                logger.warn("Received empty or invalid response from Alpha Vantage for symbol: {}", apiSymbol);
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Failed to fetch live price for symbol " + apiSymbol, e);
            return Optional.empty(); // Return empty if the API call fails
        }
    }
}
