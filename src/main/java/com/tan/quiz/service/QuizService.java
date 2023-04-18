package com.tan.quiz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tan.quiz.model.Completion;
import com.tan.quiz.model.CompletionRepository;
import com.tan.quiz.model.Quiz;
import com.tan.quiz.model.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class QuizService {
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    CompletionRepository completionRepository;

    public int addQuiz(Quiz quiz) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        quiz.setEmail(auth.getName());
        return quizRepository.save(quiz).getId();
    }

    public boolean containsId(int id) {
        for(var tmp: quizRepository.findAll()) {
            if(tmp.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public String getQuizJsonById(int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilter", SimpleBeanPropertyFilter.serializeAllExcept(Set.of("answer","email")));
        try {
            return objectMapper.writer(filters).writeValueAsString(quizRepository.findById(id).orElseThrow());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getQuizJsonAll() {
        List<Quiz> quizzes = new ArrayList<>();
        for(var tmp: quizRepository.findAll()) {
            quizzes.add(tmp);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilter", SimpleBeanPropertyFilter.serializeAll());
        try {
            return objectMapper.writer(filters).writeValueAsString(quizzes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getQuizPageJsonAll(int page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<Quiz> pageQuiz = quizRepository.findAll(pageable);
        ObjectMapper objectMapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilter", SimpleBeanPropertyFilter.serializeAllExcept(Set.of("answer","email")));
        try {
            return objectMapper.writer(filters).writeValueAsString(pageQuiz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String getCompletionPageJsonAll(int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(page,10, Sort.by("completedAt").descending());
        Page<Completion> pageCompletion = completionRepository.findAllByEmail(auth.getName(), pageable);
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule jtm = new JavaTimeModule();
        jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        objectMapper.registerModule(jtm);
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilterCompletion", SimpleBeanPropertyFilter.serializeAllExcept(Set.of("complete_id","email")));
        try {
            return objectMapper.writer(filters).writeValueAsString(pageCompletion);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean checkQuizAnswer(int id, List<Integer> answer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(new ArrayList<>(quizRepository.findById(id).orElseThrow().getAnswer()).equals(answer)) {
            Completion completion = new Completion();
            completion.setId(id);
            completion.setCompletedAt(LocalDateTime.now());
            completion.setEmail(auth.getName());
            completionRepository.save(completion);
            return true;
        }
        return false;
    }

    public boolean deleteQuiz(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!quizRepository.findById(id).orElseThrow().getEmail().equals(auth.getName())) {
            return false;
        }
        quizRepository.deleteById(id);
        return true;
    }

}
