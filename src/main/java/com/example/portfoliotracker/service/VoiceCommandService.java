package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.TradeDto;
import com.example.portfoliotracker.entity.Portfolio;
import com.example.portfoliotracker.entity.Symbol;
import com.example.portfoliotracker.entity.TradeSide;
import com.example.portfoliotracker.repository.PortfolioRepository;
import com.example.portfoliotracker.repository.SymbolRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class VoiceCommandService {

    private final TradeService tradeService;
    private final SymbolRepository symbolRepository;
    private final PortfolioRepository portfolioRepository;
    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    @Value("${GEMINI_API_URL}")
    private String geminiApiUrl;

    public VoiceCommandService(TradeService tradeService, SymbolRepository symbolRepository,
            PortfolioRepository portfolioRepository) {
        this.tradeService = tradeService;
        this.symbolRepository = symbolRepository;
        this.portfolioRepository = portfolioRepository;
        this.restTemplate = new RestTemplate();
    }

    // In VoiceCommandService.java

    public void processCommand(String text) throws Exception {
        String systemPrompt = "You are a financial assistant for a stock portfolio app. " +
                "Analyze the user's text and extract the action (BUY or SELL), the quantity, and the stock ticker. " +
                "If the quantity is not mentioned, assume the quantity is 1. " + // Added this instruction
                "The stock ticker must be one of the following: RELIANCE, TCS, HDFCBANK, INFY, ICICIBANK, HINDUNILVR, SBIN, BAJFINANCE. "
                +
                "Your response MUST be only a valid JSON object with the keys 'action', 'quantity', and 'ticker'. " +
                "For example, if the user says 'buy 10 shares of tcs', you must respond with: " +
                "{\"action\": \"BUY\", \"quantity\": 10, \"ticker\": \"TCS\"}";

        // --- THIS IS THE FIX ---
        // Using a proper JSON library (like Jackson's ObjectMapper) to build the
        // payload
        // is safer than manual string formatting because it handles escaping
        // automatically.
        // However, a simple fix for the current code is to escape the quotes manually.

        // A simple manual escaping fix:
        String escapedSystemPrompt = systemPrompt.replace("\"", "\\\"");

        String requestBody = String.format(
                "{\"contents\":[{\"parts\":[{\"text\": \"%s\"}, {\"text\": \"User command: %s\"}]}]}",
                escapedSystemPrompt, // Use the escaped version
                text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(fullUrl, entity, String.class);

        // ... rest of the method remains the same ...

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        // This part can be fragile, a safer way is to look for the text part.
        String jsonContent = root.at("/candidates/0/content/parts/0/text").asText();

        JsonNode commandJson = mapper.readTree(jsonContent);

        String action = commandJson.path("action").asText();
        String ticker = commandJson.path("ticker").asText();
        BigDecimal quantity = new BigDecimal(commandJson.path("quantity").asInt());

        Symbol symbol = symbolRepository.findByTicker(ticker)
                .orElseThrow(() -> new EntityNotFoundException("Symbol not found for ticker: " + ticker));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio portfolio = portfolioRepository.findFirstByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("No portfolio found for the user."));

        // For now, we are still using a placeholder price for simplicity.
        BigDecimal price = new BigDecimal("100.00");

        TradeDto tradeDto = TradeDto.builder()
                .side(TradeSide.valueOf(action))
                .quantity(quantity)
                .price(price)
                .tradeDatetime(LocalDateTime.now())
                .build();

        tradeService.createTrade(tradeDto, portfolio.getId(), symbol.getId());
    }
}
