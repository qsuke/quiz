package com.tan.quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tan.quiz.model.Quiz;
import com.tan.quiz.model.User;
import com.tan.quiz.service.QuizAnswerRequest;
import com.tan.quiz.service.QuizResult;
import com.tan.quiz.service.QuizService;
import com.tan.quiz.service.UserDetailsServiceImpl;

import jakarta.validation.Valid;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/api/quizzes")
    public String postQuizzes(@Valid @RequestBody Quiz quiz) {
        return quizService.getQuizJsonById(quizService.addQuiz(quiz));
    }

    @GetMapping("/api/quizzes/{id}")
    public String getQuiz(@PathVariable int id) {
        if(!quizService.containsId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"quiz not found");
        }
        return quizService.getQuizJsonById(id);
    }

    @GetMapping("/api/quizzes_old")
    public String getQuizzes_old() {
        return quizService.getQuizJsonAll();
    }

    @GetMapping("/api/quizzes")
    public String getQuizzes(@RequestParam(defaultValue = "0") String page) {
        return quizService.getQuizPageJsonAll(Integer.valueOf(page));
    }

    @GetMapping("/api/quizzes/completed")
    public String getCompleted(@RequestParam(defaultValue = "0") String page) {
        return quizService.getCompletionPageJsonAll(Integer.valueOf(page));
    }

    @PostMapping("/api/quizzes/{id}/solve")
    public QuizResult responseQuizResult(@PathVariable int id, @RequestBody QuizAnswerRequest answer) {
        if(!quizService.containsId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"quiz not found");
        }
        if(quizService.checkQuizAnswer(id, answer.getAnswer())) {
            return new QuizResult(true, "Congratulations, you are right!!");
        }
        return new QuizResult(false, "Wrong answer! Please, try again.");
    }

    @PostMapping("/api/register")
    public void register(@Valid @RequestBody User user) {
        if(userDetailsServiceImpl.containsId(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is already taken");
        }
        userDetailsServiceImpl.saveUser(user);
    }

    @DeleteMapping("/api/quizzes/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable int id) {
        if(!quizService.containsId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"quiz not found");
        }
        if(!quizService.deleteQuiz(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("no content");
    }


}
