package com.example.portfoliotracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lots")
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_buy_id", nullable = false)
    private Trade tradeBuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_sell_id")
    private Trade tradeSell;

    @Column(nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;
    
    @Column(name = "realized_pl")
    private BigDecimal realizedPl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
