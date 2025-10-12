package com.example.portfoliotracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Add this import
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "symbols")
// --- THIS IS THE FIX ---
// This annotation tells Jackson to ignore Hibernate's internal proxy handler
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private String exchange;
    private String currency;
    private String name;
}

