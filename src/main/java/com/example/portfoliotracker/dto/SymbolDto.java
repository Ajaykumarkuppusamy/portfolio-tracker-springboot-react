package com.example.portfoliotracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolDto {
    private Long id;
    private String ticker;
    private String name;
    private String exchange;
    private String currency;
}
