package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findByTradeBuyPortfolioId(Long portfolioId);
}
