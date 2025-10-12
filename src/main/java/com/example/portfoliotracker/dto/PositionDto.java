package com.example.portfoliotracker.dto;

import com.example.portfoliotracker.entity.Symbol;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class PositionDto {
    private Symbol symbol;
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal averageCost = BigDecimal.ZERO;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal lastPrice;
    private BigDecimal marketValue;
    private BigDecimal unrealizedPL;
    private BigDecimal unrealizedPLPercent;

    public PositionDto(Symbol symbol) {
        this.symbol = symbol;
    }

    public void add(BigDecimal qty, BigDecimal price) {
        BigDecimal newCost = qty.multiply(price);
        this.totalCost = this.totalCost.add(newCost);
        this.quantity = this.quantity.add(qty);
        if (this.quantity.compareTo(BigDecimal.ZERO) != 0) {
            this.averageCost = this.totalCost.divide(this.quantity, 6, RoundingMode.HALF_UP);
        }
    }

    public void subtract(BigDecimal qty) {
        this.quantity = this.quantity.subtract(qty);
        // Recalculate total cost based on new quantity and average cost
        this.totalCost = this.quantity.multiply(this.averageCost);
        if (this.quantity.compareTo(BigDecimal.ZERO) == 0) {
            this.averageCost = BigDecimal.ZERO;
            this.totalCost = BigDecimal.ZERO;
        }
    }

    public void calculateUnrealizedPL() {
        if (lastPrice != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
            this.marketValue = this.quantity.multiply(this.lastPrice);
            this.unrealizedPL = this.marketValue.subtract(this.totalCost);
            if (this.totalCost.compareTo(BigDecimal.ZERO) != 0) {
                this.unrealizedPLPercent = this.unrealizedPL.divide(this.totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            }
        } else {
            this.marketValue = BigDecimal.ZERO;
            this.unrealizedPL = BigDecimal.ZERO;
            this.unrealizedPLPercent = BigDecimal.ZERO;
        }
    }
}
