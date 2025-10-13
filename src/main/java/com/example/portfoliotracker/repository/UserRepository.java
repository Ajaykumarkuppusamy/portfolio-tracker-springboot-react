package com.example.portfoliotracker.repository;

import com.example.portfoliotracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This method is needed by the security service and our new portfolio service
    Optional<User> findByEmail(String email);
}

