package com.mcqportal.controller;

import com.mcqportal.entity.User;
import com.mcqportal.repository.CertificateRepository;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import com.mcqportal.repository.UserRepository;
import com.mcqportal.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController extends ControllerSupport {
    private static final List<String> QUIZ_TOPICS = List.of("Cybersecurity", "Web Development", "UI/UX", "Java");

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final CertificateRepository certificateRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, QuestionRepository questionRepository,
                          ResultRepository resultRepository, CertificateRepository certificateRepository,
                          UserService userService) {
        super(userRepository);
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.certificateRepository = certificateRepository;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        var results = resultRepository.findByUserOrderByExamDateDesc(user);
        model.addAttribute("user", user);
        model.addAttribute("quizTopics", QUIZ_TOPICS);
        model.addAttribute("totalQuizzes", QUIZ_TOPICS.size());
        model.addAttribute("attemptedQuizzes", results.size());
        model.addAttribute("latestResult", results.isEmpty() ? null : results.get(0));
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("results", resultRepository.findByUserOrderByExamDateDesc(user));
        model.addAttribute("certificates", certificateRepository.findByUserOrderByIssueDateDesc(user));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication authentication, @RequestParam String fullname,
                                @RequestParam String email, @RequestParam(required = false) String newPassword,
                                Model model) {
        User user = currentUser(authentication);
        try {
            userService.updateProfile(user, fullname, email, newPassword);
            model.addAttribute("success", "Profile updated successfully.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("user", currentUser(authentication));
        model.addAttribute("results", resultRepository.findByUserOrderByExamDateDesc(user));
        model.addAttribute("certificates", certificateRepository.findByUserOrderByIssueDateDesc(user));
        return "user/profile";
    }
}
