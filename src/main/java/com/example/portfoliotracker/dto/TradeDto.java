package com.example.portfoliotracker.dto;

import com.example.portfoliotracker.entity.TradeSide;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeDto {
    private Long id;
    private String symbolTicker;
    private String symbolName;
    private TradeSide side;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime tradeDatetime;
    private BigDecimal fees;
    private String reason;
    private String tag;
}

