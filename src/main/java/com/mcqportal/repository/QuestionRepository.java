package com.mcqportal.repository;

import com.mcqportal.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByQuestionContainingIgnoreCase(String query, Pageable pageable);
    List<Question> findTop50ByCategoryIgnoreCaseOrderByIdAsc(String category);
    long countByCategoryIgnoreCase(String category);
}
