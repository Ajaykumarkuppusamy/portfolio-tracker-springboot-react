package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    // --- THIS IS THE NEW PART ---
    // This adds the required method, fixing the findByUserId() error.
    List<Portfolio> findByUserId(Long userId);
}

