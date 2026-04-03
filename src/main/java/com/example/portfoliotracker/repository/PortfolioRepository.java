package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // --- THIS IS THE NEW PART ---
    // This adds the required method, fixing the findByUserId() error.
    List<Portfolio> findByUserId(Long userId);

    @Query("SELECT p FROM Portfolio p WHERE p.user.email = :email")
    Optional<Portfolio> findFirstByEmail(@Param("email") String email);
}
