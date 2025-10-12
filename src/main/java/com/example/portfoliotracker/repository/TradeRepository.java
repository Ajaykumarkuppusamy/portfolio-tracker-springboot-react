package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByPortfolioIdOrderByTradeDatetimeAsc(Long portfolioId);
}
