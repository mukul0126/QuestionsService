package com.example.QuestionsService.controllers;


import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.services.AnswerService;
import com.example.QuestionsService.services.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
@CrossOrigin(origins = "*",allowedHeaders = "*")

public class AnswersConroller {
    @Autowired
    AnswerService answerService;
    @PostMapping(value = "/add")
    public ResponseEntity<String> add(@RequestBody AnswerDto answerDto){

       Answer answer1=answerService.save(answerDto);
        return new ResponseEntity<String>(answer1.getAnswerId(),HttpStatus.CREATED);
    }
//take dto and add code for notification
    @PutMapping(value = "/likeanswer/{answerId}")
    public  String likeAnswer(@PathVariable("answerId") String answerId){
        return  answerService.like(answerId);
    }

//take dto and add code for notification
    @PutMapping(value = "/dislikeanswer/{answerId}")
    public  String dislikeAnswer(@PathVariable("answerId") String answerId){
        return  answerService.disLike(answerId);
    }
}
