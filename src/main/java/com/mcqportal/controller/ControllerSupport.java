package com.mcqportal.controller;

import com.mcqportal.entity.User;
import com.mcqportal.repository.UserRepository;
import org.springframework.security.core.Authentication;

public abstract class ControllerSupport {
    protected final UserRepository userRepository;

    protected ControllerSupport(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }
}
