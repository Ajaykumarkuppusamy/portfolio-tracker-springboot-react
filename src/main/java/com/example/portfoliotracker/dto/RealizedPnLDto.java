package com.example.portfoliotracker.dto;

import com.example.portfoliotracker.entity.Symbol;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RealizedPnLDto {
    private Symbol symbol;
    private LocalDateTime sellDate;
    private BigDecimal quantity;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal realizedPL;
}
