package com.example.QuestionsService.controllers;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import com.example.QuestionsService.dtos.requestdto.CategoryDto;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.responsedto.FeedDto;
import com.example.QuestionsService.dtos.responsedto.QuestionListDto;
import com.example.QuestionsService.entities.Category;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.services.AnswerService;
import com.example.QuestionsService.services.CategoryService;
import com.example.QuestionsService.services.QuestionService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@RequestMapping("/question")
public class QuestionsController {

    @Autowired
    QuestionService questionService;
    @Autowired
    CategoryService categoryService;
    @PostMapping(value = "/add")
    public ResponseEntity<String> add(@RequestBody QuestionDto question){


        Question question2=questionService.save(question);
        return new ResponseEntity<String>(question2.getQuestionId(),HttpStatus.CREATED);
    }

    @PutMapping(value = "/likequestion/{questionId}")
    public  String likeQuestion(@PathVariable("questionId") String questionId){
        return  questionService.like(questionId);
    }
    @PutMapping(value = "/dislikequestion/{questionId}")
    public  String dislikeQuestion(@PathVariable("questionId") String questionId){
        return  questionService.disLike(questionId);
    }
    @PostMapping(value = "/addcategory")
    public Category addcategory(@RequestBody CategoryDto categoryDto){
        return  categoryService.createCategory(categoryDto);
    }
    @GetMapping(value = "/getLoginFeed/{userId}")
    public List<Question> getLoginFeed(@PathVariable("userId") String userId){

        return questionService.getFeed(userId);
    }
    @GetMapping(value = "/getQuestionsByUserId/{userId}")
    public QuestionListDto getQuestionsByUserId(@PathVariable("userId") String userid){
        return  questionService.getQuestionsById(userid);
    }
    @GetMapping(value = "/getWithoutLoginFeed")
    public QuestionListDto getAllQuestions(){
        return questionService.getAllQuestions();
    }
    @GetMapping(value = "/getQuestionsByUserIdApproved/{userId}")
    public QuestionListDto getQuestionsByUserIdApproved(@PathVariable("userId")  String userId){
        return questionService.getQuestionsByUserIdApproved(userId);
    }



}
