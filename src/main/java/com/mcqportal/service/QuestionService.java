package com.mcqportal.service;

import com.mcqportal.entity.Question;
import com.mcqportal.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Page<Question> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return questionRepository.findAll(pageable);
        }
        return questionRepository.findByQuestionContainingIgnoreCase(query.trim(), pageable);
    }

    public List<Question> allForQuiz() {
        return questionRepository.findAll();
    }

    public Question save(Question question) {
        if (question.getCategory() == null || question.getCategory().isBlank()) {
            throw new IllegalArgumentException("Please select a question category.");
        }
        String answer = question.getCorrectAnswer();
        if (answer == null || !answer.matches("[ABCD]")) {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D.");
        }
        return questionRepository.save(question);
    }
}
