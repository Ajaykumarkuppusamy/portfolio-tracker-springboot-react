package com.example.portfoliotracker.service;

import com.example.portfoliotracker.entity.Lot;
import com.example.portfoliotracker.entity.Trade;
import com.example.portfoliotracker.entity.TradeSide; // Import the standalone enum
import com.example.portfoliotracker.repository.LotRepository;
import com.example.portfoliotracker.repository.TradeRepository;
import com.example.portfoliotracker.dto.PositionDto;
import com.example.portfoliotracker.dto.RealizedPnLDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioCalculatorService {

    private final TradeRepository tradeRepository;
    private final LotRepository lotRepository;
    private final QuoteService quoteService;

    @Transactional(readOnly = true)
    public List<PositionDto> calculatePositions(Long portfolioId) {
        List<Trade> trades = tradeRepository.findByPortfolioIdOrderByTradeDatetimeAsc(portfolioId);
        Map<Long, PositionDto> positions = new HashMap<>();

        for (Trade trade : trades) {
            Long symbolId = trade.getSymbol().getId();
            positions.putIfAbsent(symbolId, new PositionDto(trade.getSymbol()));
            
            PositionDto position = positions.get(symbolId);
            // --- FIX ---
            // Refer to the standalone TradeSide enum
            if (trade.getSide() == TradeSide.BUY) {
                position.add(trade.getQuantity(), trade.getPrice());
            } else { // SELL
                position.subtract(trade.getQuantity());
            }
        }
        
        positions.values().forEach(pos -> {
            quoteService.getLatestPrice(pos.getSymbol()).ifPresent(priceTick -> {
                pos.setLastPrice(priceTick.getLast());
                pos.calculateUnrealizedPL();
            });
        });

        // Filter out positions with zero quantity
        return positions.values().stream()
                .filter(p -> p.getQuantity().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processLotsForSellTrade(Trade sellTrade) {
        // This is a simplified FIFO implementation.
        List<Trade> buys = tradeRepository.findByPortfolioIdAndSymbolIdAndSideOrderByTradeDatetimeAsc(
            sellTrade.getPortfolio().getId(),
            sellTrade.getSymbol().getId(),
            TradeSide.BUY // --- FIX ---
        );

        List<Lot> lots = lotRepository.findByTradeBuyPortfolioIdAndTradeBuySymbolId(
            sellTrade.getPortfolio().getId(),
            sellTrade.getSymbol().getId()
        );

        Map<Long, BigDecimal> soldQuantities = new HashMap<>();
        lots.forEach(lot -> soldQuantities.merge(lot.getTradeBuy().getId(), lot.getQuantity(), BigDecimal::add));

        BigDecimal sellQuantityRemaining = sellTrade.getQuantity();

        for(Trade buy : buys) {
            if (sellQuantityRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal boughtQty = buy.getQuantity();
            BigDecimal soldQty = soldQuantities.getOrDefault(buy.getId(), BigDecimal.ZERO);
            BigDecimal availableQty = boughtQty.subtract(soldQty);

            if (availableQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal qtyToMatch = sellQuantityRemaining.min(availableQty);
                
                BigDecimal cost = buy.getPrice().multiply(qtyToMatch);
                BigDecimal proceeds = sellTrade.getPrice().multiply(qtyToMatch);
                BigDecimal pnl = proceeds.subtract(cost);

                Lot newLot = Lot.builder()
                        .tradeBuy(buy)
                        .tradeSell(sellTrade)
                        .quantity(qtyToMatch)
                        .costPrice(buy.getPrice())
                        .realizedPl(pnl)
                        .build();
                lotRepository.save(newLot);

                sellQuantityRemaining = sellQuantityRemaining.subtract(qtyToMatch);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<RealizedPnLDto> calculateRealizedPnL(Long portfolioId) {
        return lotRepository.findByTradeSellPortfolioId(portfolioId) // Corrected logic to fetch lots by sell trade
            .stream()
            .filter(lot -> lot.getRealizedPl() != null)
            .map(lot -> new RealizedPnLDto(
                lot.getTradeBuy().getSymbol(),
                lot.getTradeSell().getTradeDatetime(),
                lot.getQuantity(),
                lot.getCostPrice(),
                lot.getTradeSell().getPrice(),
                lot.getRealizedPl()
            ))
            .collect(Collectors.toList());
    }
}

