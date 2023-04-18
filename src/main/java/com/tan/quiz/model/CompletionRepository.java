package com.tan.quiz.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompletionRepository extends PagingAndSortingRepository<Completion, Integer>,CrudRepository<Completion, Integer> {
    Page<Completion> findAllByEmail(String email, Pageable pageable);
}
