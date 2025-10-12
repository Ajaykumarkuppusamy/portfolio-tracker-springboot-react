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

    @Transactional
    public PortfolioDto createPortfolio(PortfolioDto portfolioDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .name(portfolioDto.getName())
                .baseCurrency(portfolioDto.getBaseCurrency() != null ? portfolioDto.getBaseCurrency() : "INR")
                .build();

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        return mapToDto(savedPortfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getPortfoliosForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        return user.getPortfolios().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    // Add this to your Portfolio entity to make the above call work
    // In Portfolio.java, add:
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Portfolio> portfolios = new ArrayList<>();
    //
    // For now, we will query the repository directly to avoid changing the entity
    @Transactional(readOnly = true)
    public List<PortfolioDto> findPortfoliosByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
        return portfolioRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private PortfolioDto mapToDto(Portfolio portfolio) {
        return new PortfolioDto(portfolio.getId(), portfolio.getName(), portfolio.getBaseCurrency());
    }
}
