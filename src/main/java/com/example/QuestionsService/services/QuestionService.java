package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.responsedto.FeedDto;
import com.example.QuestionsService.dtos.responsedto.QuestionListDto;
import com.example.QuestionsService.entities.Question;

import java.util.List;
import java.util.Set;

public interface QuestionService {

    Question save(QuestionDto question);
    String like(String questionid);
    String disLike(String questionid);
    Boolean addAnswer(String questionId,String answerId);

   List<Question> getFeed(String userId);

    QuestionListDto getQuestionsById(String userid);

    QuestionListDto getAllQuestions();

    QuestionListDto getQuestionsByUserIdApproved(String userId);
}