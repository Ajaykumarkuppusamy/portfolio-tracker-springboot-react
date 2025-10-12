package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Cashflow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CashflowRepository extends JpaRepository<Cashflow, Long> {
    List<Cashflow> findByPortfolioId(Long portfolioId);
}
