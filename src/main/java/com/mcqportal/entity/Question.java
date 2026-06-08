package com.mcqportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String question;

    @Column(length = 80)
    private String category;

    @NotBlank
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @NotBlank
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @NotBlank
    @Column(name = "option_c", nullable = false)
    private String optionC;

    @NotBlank
    @Column(name = "option_d", nullable = false)
    private String optionD;

    @NotBlank
    @Column(name = "correct_answer", nullable = false, length = 1)
    private String correctAnswer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category == null ? null : category.trim(); }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer == null ? null : correctAnswer.trim().toUpperCase(); }
}
