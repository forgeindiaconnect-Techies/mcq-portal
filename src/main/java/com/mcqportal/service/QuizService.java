package com.mcqportal.service;

import com.mcqportal.entity.*;
import com.mcqportal.repository.AnswerRepository;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ResultRepository resultRepository;
    private final CertificateService certificateService;

    public QuizService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                       ResultRepository resultRepository, CertificateService certificateService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.resultRepository = resultRepository;
        this.certificateService = certificateService;
    }

    @Transactional
    public Result submit(User user, Map<Long, String> selectedAnswers) {
        return submit(user, selectedAnswers, null);
    }

    @Transactional
    public Result submit(User user, Map<Long, String> selectedAnswers, String category) {
        List<Question> questions = (category == null || category.isBlank())
                ? questionRepository.findAll()
                : questionRepository.findTop50ByCategoryIgnoreCaseOrderByIdAsc(category);
        int attempted = 0;
        int correct = 0;

        Result result = new Result();
        result.setUser(user);
        result.setTotalQuestions(questions.size());
        result.setExamDate(LocalDateTime.now());

        for (Question question : questions) {
            String selected = Optional.ofNullable(selectedAnswers.get(question.getId()))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .filter(value -> value.matches("[ABCD]"))
                    .orElse("");
            if (!selected.isBlank()) {
                attempted++;
                if (selected.equals(question.getCorrectAnswer())) {
                    correct++;
                }
            }
        }

        int wrong = attempted - correct;
        double percentage = questions.isEmpty() ? 0 : (correct * 100.0 / questions.size());
        result.setAttemptedQuestions(attempted);
        result.setCorrectAnswers(correct);
        result.setWrongAnswers(wrong);
        result.setScore(correct);
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);
        result.setStatus(result.getPercentage() >= 50 ? ResultStatus.PASS : ResultStatus.FAIL);
        Result saved = resultRepository.save(result);

        for (Question question : questions) {
            Answer answer = new Answer();
            answer.setUser(user);
            answer.setQuestion(question);
            answer.setSelectedAnswer(selectedAnswers.get(question.getId()));
            answer.setResult(saved);
            answerRepository.save(answer);
        }
        if (saved.getStatus() == ResultStatus.PASS) {
            certificateService.generateForResult(saved);
        }
        return saved;
    }
}
