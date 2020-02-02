package com.example.QuestionsService.controllers;


import com.example.QuestionsService.dtos.requestdto.CategoryDto;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.requestdto.QuestionIdsDTO;
import com.example.QuestionsService.dtos.responsedto.QuestionListDto;
import com.example.QuestionsService.dtos.responsedto.ResponseString;
import com.example.QuestionsService.entities.Category;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.services.CategoryService;
import com.example.QuestionsService.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@RequestMapping("/question")
public class QuestionsController {

    @Autowired
    QuestionService questionService;
    @Autowired
    CategoryService categoryService;
    @PostMapping(value = "/add")
    public ResponseString add(@RequestBody QuestionDto question){
        Question question2=questionService.save(question);
        ResponseString responseString=new ResponseString();
        responseString.setId(question2.getQuestionId());
        return  responseString;

    }

    @PutMapping(value = "/likequestion/{questionId}/{userId}")
    public  ResponseString likeQuestion(@PathVariable("questionId") String questionId ,@PathVariable("userId") String userId){
       String id=  questionService.like(questionId,userId);
       ResponseString responseString=new ResponseString();
       responseString.setId(id);
       return  responseString;
    }
    @PutMapping(value = "/dislikequestion/{questionId}/{userId}")
    public  ResponseString dislikeQuestion(@PathVariable("questionId") String questionId , @PathVariable("userId") String  userId){
        String id=  questionService.disLike(questionId,userId);
        ResponseString responseString=new ResponseString();
        responseString.setId(id);
        return  responseString;
    }
    @PostMapping(value = "/addcategory")
    public Category addcategory(@RequestBody CategoryDto categoryDto){
        return  categoryService.createCategory(categoryDto);
    }
    @GetMapping(value = "/getLoginFeed/{userId}")
    public List<Question> getLoginFeed(@PathVariable("userId") String userId){

        return questionService.getFeed(userId);
    }
    @GetMapping(value = "/getLoginFeedPaginated/{userId}")
    public List<Question> getLoginFeedPaginated(@PathVariable("userId") String userId) throws ExecutionException, InterruptedException{

        return questionService.getFeedPaginated(userId);
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
    @PutMapping(value = "/approveQuestionByModerator/{questionId}")
    public Boolean approveQuestionByModerator(@PathVariable("questionId")  String questionId){
        return  questionService.approveQuestionByModerator(questionId);
    }
    @PutMapping(value = "/disapproveQuestionByModerator/{questionId}")
    public Boolean disapproveQuestionByModerator(@PathVariable("questionId")  String questionId){
        return  questionService.disapproveQuestionByModerator(questionId);
    }
    @PutMapping(value="/choosingBestAnswer/{questionId}/{answerId}")
    public Boolean choosingBestAnswer(@PathVariable("questionId") String questionId,@PathVariable("answerId") String answerId){
        return  questionService.choosingBestAnswer(questionId,answerId);
    }
    @GetMapping(value = "/getQuestionByOrganiztionId/{organizationId}")
   public  QuestionListDto getQuestionByOrganiztionId(@PathVariable("organizationId") String organizationId){
        return  questionService.getQuestionByOrganiztionId(organizationId);
    }
    @PostMapping(value="/getAllQuestion")
    QuestionListDto getAllQuestionForApproval(@RequestBody QuestionIdsDTO questionList ){
       return questionService.getAllQuestionForApproval(questionList);
    }
    @PostMapping(value="/getQuestionsByCategory")
    QuestionListDto  getQuestionsByCategory(@RequestBody QuestionIdsDTO questionIdsDTO){

        System.out.println(questionIdsDTO);
        return questionService.getQuestionsByCategory(questionIdsDTO);
    }



}
