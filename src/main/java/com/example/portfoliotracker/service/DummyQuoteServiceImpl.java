// package com.example.portfoliotracker.service;

// import com.example.portfoliotracker.entity.PriceTick;
// import com.example.portfoliotracker.entity.Symbol;
// import org.springframework.stereotype.Service;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.LocalDateTime;
// import java.util.Optional;
// import java.util.Random;

// @Service
// public class DummyQuoteServiceImpl implements QuoteService {

//     private final Random random = new Random();

//     @Override
//     public Optional<PriceTick> getLatestPrice(Symbol symbol) {
//         // Generate a random price for demonstration purposes
//         double randomFactor = 50 + (5000 - 50) * random.nextDouble();
//         BigDecimal price = BigDecimal.valueOf(randomFactor).setScale(2, RoundingMode.HALF_UP);

//         PriceTick tick = PriceTick.builder()
//                 .symbol(symbol)
//                 .asOf(LocalDateTime.now())
//                 .last(price)
//                 .prevClose(price.multiply(BigDecimal.valueOf(0.98))) // ~2% less
//                 .dayOpen(price.multiply(BigDecimal.valueOf(0.99))) // ~1% less
//                 .dayHigh(price.multiply(BigDecimal.valueOf(1.02))) // ~2% more
//                 .dayLow(price.multiply(BigDecimal.valueOf(0.97))) // ~3% less
//                 .volume(random.longs(10000, 1000000).findFirst().getAsLong())
//                 .build();
        
//         return Optional.of(tick);
//     }
// }