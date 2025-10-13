package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.TradeDto;
import com.example.portfoliotracker.entity.Portfolio;
import com.example.portfoliotracker.entity.Symbol;
import com.example.portfoliotracker.entity.Trade;
import com.example.portfoliotracker.entity.TradeSide;
import com.example.portfoliotracker.repository.PortfolioRepository;
import com.example.portfoliotracker.repository.SymbolRepository;
import com.example.portfoliotracker.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final PortfolioRepository portfolioRepository;
    private final SymbolRepository symbolRepository;

    public TradeService(TradeRepository tradeRepository, PortfolioRepository portfolioRepository, SymbolRepository symbolRepository) {
        this.tradeRepository = tradeRepository;
        this.portfolioRepository = portfolioRepository;
        this.symbolRepository = symbolRepository;
    }

    @Transactional
    public TradeDto createTrade(TradeDto tradeDto, Long portfolioId, Long symbolId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Symbol symbol = symbolRepository.findById(symbolId)
                .orElseThrow(() -> new EntityNotFoundException("Symbol not found with id: " + symbolId));

        Trade trade = new Trade();
        trade.setPortfolio(portfolio);
        trade.setSymbol(symbol);
        // --- FIX #1: Pass the enum directly, no need for valueOf ---
        trade.setSide(tradeDto.getSide());
        trade.setQuantity(tradeDto.getQuantity());
        trade.setPrice(tradeDto.getPrice());
        trade.setTradeDatetime(tradeDto.getTradeDatetime());
        trade.setFees(tradeDto.getFees());
        trade.setReason(tradeDto.getReason());
        trade.setTag(tradeDto.getTag());

        Trade savedTrade = tradeRepository.save(trade);
        return convertToDto(savedTrade);
    }

    @Transactional(readOnly = true)
    public List<TradeDto> getTradesForPortfolio(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        return tradeRepository.findByPortfolioIdOrderByTradeDatetimeDesc(portfolioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

     public void deleteTrade(Long tradeId) {
        if (!tradeRepository.existsById(tradeId)) {
            throw new EntityNotFoundException("Trade not found with id: " + tradeId);
        }
        tradeRepository.deleteById(tradeId);
    }


    private TradeDto convertToDto(Trade trade) {
        return TradeDto.builder()
                .id(trade.getId())
                .symbolTicker(trade.getSymbol().getTicker())
                .symbolName(trade.getSymbol().getName())
                // --- FIX #2: Pass the enum directly, no need for toString() ---
                .side(trade.getSide())
                .quantity(trade.getQuantity())
                .price(trade.getPrice())
                .tradeDatetime(trade.getTradeDatetime())
                .fees(trade.getFees())
                .reason(trade.getReason())
                .tag(trade.getTag())
                .build();
    }
}

