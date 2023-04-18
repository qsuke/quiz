package com.tan.quiz.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonFilter("myFilterCompletion")
public class Completion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int complete_id;
    private int id;
    private LocalDateTime completedAt;
    private String email;

}
