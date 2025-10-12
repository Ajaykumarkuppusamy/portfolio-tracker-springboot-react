package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.PriceTick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceTickRepository extends JpaRepository<PriceTick, Long> {
}
