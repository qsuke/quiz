package com.tan.quiz.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@JsonFilter("myFilter")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;

    private String email;

    @ElementCollection
    @NotNull
    @Size(min=2)
    private List<String> options;
    @ElementCollection
    private List<Integer> answer = new ArrayList<>();
}
