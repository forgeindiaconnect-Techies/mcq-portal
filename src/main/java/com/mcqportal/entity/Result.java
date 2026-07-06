package com.mcqportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int attemptedQuestions;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private int wrongAnswers;

    @Column(nullable = false)
    private double percentage;

    @Column(length = 80)
    private String category;

    @Column(nullable = false)
    private boolean malpracticeDetected = false;

    @Column(nullable = false)
    private int malpracticeWarnings = 0;

    @Column(length = 255)
    private String malpracticeReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status;

    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate = LocalDateTime.now();

    @OneToOne(mappedBy = "result", cascade = CascadeType.ALL)
    private Certificate certificate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getAttemptedQuestions() { return attemptedQuestions; }
    public void setAttemptedQuestions(int attemptedQuestions) { this.attemptedQuestions = attemptedQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isMalpracticeDetected() { return malpracticeDetected; }
    public void setMalpracticeDetected(boolean malpracticeDetected) { this.malpracticeDetected = malpracticeDetected; }
    public int getMalpracticeWarnings() { return malpracticeWarnings; }
    public void setMalpracticeWarnings(int malpracticeWarnings) { this.malpracticeWarnings = malpracticeWarnings; }
    public String getMalpracticeReason() { return malpracticeReason; }
    public void setMalpracticeReason(String malpracticeReason) { this.malpracticeReason = malpracticeReason; }
    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }
    public LocalDateTime getExamDate() { return examDate; }
    public void setExamDate(LocalDateTime examDate) { this.examDate = examDate; }
    public Certificate getCertificate() { return certificate; }
    public void setCertificate(Certificate certificate) { this.certificate = certificate; }
}
