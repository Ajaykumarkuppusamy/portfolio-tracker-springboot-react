package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {

    // Keep this original method, it might be used elsewhere
    List<Lot> findByTradeBuyPortfolioId(Long portfolioId);

    // Add this missing method
    List<Lot> findByTradeBuyPortfolioIdAndTradeBuySymbolId(Long portfolioId, Long symbolId);

    // Add this missing method
    List<Lot> findByTradeSellPortfolioId(Long portfolioId);
}

