package com.example.QuestionsService.controllers;


import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.dtos.requestdto.AnswerIdsDTO;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.responsedto.AnswerListDto;
import com.example.QuestionsService.dtos.responsedto.QuestionAnswerDto;
import com.example.QuestionsService.dtos.responsedto.ResponseString;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.services.AnswerService;
import com.example.QuestionsService.services.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answer")
@CrossOrigin(origins = "*",allowedHeaders = "*")

public class AnswersConroller {
    @Autowired
    AnswerService answerService;
    @PostMapping(value = "/add")
    public  ResponseString add(@RequestBody AnswerDto answerDto){

       Answer answer1=answerService.save(answerDto);
        String id=answer1.getAnswerId();
        ResponseString responseString=new ResponseString();
        responseString.setId(id);
        return  responseString;
    }
//take dto and add code for notification
    @PutMapping(value = "/likeanswer/{answerId}/{userId}")
    public  ResponseString likeAnswer(@PathVariable("answerId") String answerId,@PathVariable("userId") String userId ){
        String id =answerService.like(answerId,userId);
        ResponseString responseString=new ResponseString();
        responseString.setId(id);
        return  responseString;
    }

//take dto and add code for notification
    @PutMapping(value = "/dislikeanswer/{answerId}/{userId}")
    public  ResponseString dislikeAnswer(@PathVariable("answerId") String answerId, @PathVariable("userId") String userId){
        String id=  answerService.disLike(answerId,userId);
        ResponseString responseString=new ResponseString();
        responseString.setId(id);
        return  responseString;
    }

    @GetMapping(value = "/getAnswersByUserId/{userId}")
    public List<Answer> getAnswersByUserId(@PathVariable("userId") String UserId){
        return answerService.getAnswersByUserId(UserId);
    }

    @GetMapping(value = "/getAnswersByQuestionId/{questionId}")
    public QuestionAnswerDto getAnswersByQuestionId(@PathVariable("questionId") String questionId){
        return answerService.getAnswersByQuestionId(questionId);
    }
    @PutMapping(value = "approveAnswerByModerator/{answerId}")
    public Boolean approveAnswerByModerator(@PathVariable("answerId")  String answerId){
        return  answerService.approveAnswerByModerator(answerId);
    }
    @PutMapping(value = "disapproveAnswerByModerator/{answerId}")
    public Boolean disapproveAnswerByModerator(@PathVariable("answerId")  String answerId){
        return  answerService.disapproveAnswerByModerator(answerId);
    }
    @PostMapping(value="getAllAnswer")
    AnswerListDto getAllAnswerForApproval(@RequestBody AnswerIdsDTO answerList ){

        return  answerService.getAllAnswerForApproval(answerList);

    }

}
