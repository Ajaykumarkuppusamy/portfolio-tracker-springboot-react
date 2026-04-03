package com.example.portfoliotracker.controller;

import com.example.portfoliotracker.dto.VoiceCommandDto;
import com.example.portfoliotracker.service.VoiceCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class VoiceCommandController {

    private final VoiceCommandService voiceCommandService;

    public VoiceCommandController(VoiceCommandService voiceCommandService) {
        this.voiceCommandService = voiceCommandService;
    }

    @PostMapping("/voice-command")
    public ResponseEntity<String> handleVoiceCommand(@RequestBody VoiceCommandDto commandDto) {
        if (commandDto == null || commandDto.getText() == null || commandDto.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Command text cannot be empty.");
        }

        try {
            voiceCommandService.processCommand(commandDto.getText());
            return ResponseEntity.ok("Trade command processed successfully!");
        } catch (EntityNotFoundException e) {
            // Catches errors when the stock ticker is not found
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Catches errors from Gemini when the command is unclear
            return ResponseEntity.badRequest().body("Sorry, I couldn't understand the command. " + e.getMessage());
        } catch (Exception e) {
            // Generic catch-all for other unexpected errors
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }
}

