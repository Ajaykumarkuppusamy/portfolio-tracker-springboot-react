package com.example.portfoliotracker.service;

import com.example.portfoliotracker.entity.Lot;
import com.example.portfoliotracker.entity.Trade;
import com.example.portfoliotracker.repository.LotRepository;
import com.example.portfoliotracker.repository.TradeRepository;
import com.example.portfoliotracker.dto.PositionDto;
import com.example.portfoliotracker.dto.RealizedPnLDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            if (trade.getSide() == Trade.TradeSide.BUY) {
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

        return new ArrayList<>(positions.values());
    }

    @Transactional
    public void processLotsForSellTrade(Trade sellTrade) {
        // This is a simplified FIFO implementation. A real-world one would be more complex.
        List<Trade> buys = tradeRepository.findByPortfolioIdOrderByTradeDatetimeAsc(sellTrade.getPortfolio().getId())
                .stream()
                .filter(t -> t.getSymbol().equals(sellTrade.getSymbol()) && t.getSide() == Trade.TradeSide.BUY)
                .collect(Collectors.toList());

        List<Lot> lots = lotRepository.findByTradeBuyPortfolioId(sellTrade.getPortfolio().getId());

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
        return lotRepository.findByTradeBuyPortfolioId(portfolioId)
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
