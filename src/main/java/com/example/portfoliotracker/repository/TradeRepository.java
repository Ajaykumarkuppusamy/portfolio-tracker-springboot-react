package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Trade;
import com.example.portfoliotracker.entity.TradeSide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByPortfolioIdOrderByTradeDatetimeAsc(Long portfolioId);

    // Add this missing method
    List<Trade> findByPortfolioIdOrderByTradeDatetimeDesc(Long portfolioId);

    // Add this missing method
    List<Trade> findByPortfolioIdAndSymbolIdAndSideOrderByTradeDatetimeAsc(Long portfolioId, Long symbolId, TradeSide side);
}

