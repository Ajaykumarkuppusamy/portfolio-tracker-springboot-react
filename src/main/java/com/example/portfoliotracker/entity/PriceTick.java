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
@Table(name = "price_ticks")
public class PriceTick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol_id", nullable = false)
    private Symbol symbol;

    @Column(name = "as_of", nullable = false)
    private LocalDateTime asOf;

    private BigDecimal last;
    @Column(name = "day_open")
    private BigDecimal dayOpen;
    @Column(name = "day_high")
    private BigDecimal dayHigh;
    @Column(name = "day_low")
    private BigDecimal dayLow;
    @Column(name = "prev_close")
    private BigDecimal prevClose;
    private Long volume;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
