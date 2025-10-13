package com.example.portfoliotracker.service;

import com.example.portfoliotracker.dto.PortfolioDto;
import com.example.portfoliotracker.entity.Portfolio;
import com.example.portfoliotracker.entity.User;
import com.example.portfoliotracker.repository.PortfolioRepository;
import com.example.portfoliotracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    public List<PortfolioDto> getPortfoliosForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        return portfolioRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioDto createPortfolio(PortfolioDto portfolioDto, String userEmail) {
        // 1. Find the user inside the transaction
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // 2. Create and prepare the new portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setName(portfolioDto.getName());
        portfolio.setBaseCurrency(portfolioDto.getBaseCurrency());
        portfolio.setUser(user); // 3. Link it to the managed user entity

        // 4. Save the portfolio
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // 5. Return the DTO of the saved portfolio
        return convertToDto(savedPortfolio);
    }

    private PortfolioDto convertToDto(Portfolio portfolio) {
        PortfolioDto dto = new PortfolioDto();
        dto.setId(portfolio.getId());
        dto.setName(portfolio.getName());
        dto.setBaseCurrency(portfolio.getBaseCurrency());
        return dto;
    }
}

