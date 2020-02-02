package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.requestdto.QuestionIdsDTO;
import com.example.QuestionsService.dtos.responsedto.FeedDto;
import com.example.QuestionsService.dtos.responsedto.QuestionListDto;
import com.example.QuestionsService.entities.Question;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface QuestionService {

    Question save(QuestionDto question);
    String like(String questionid,String userId);
    String disLike(String questionid,String userId);
    Boolean addAnswer(String questionId,String answerId);

   List<Question> getFeed(String userId);

    QuestionListDto getQuestionsById(String userid);

    QuestionListDto getAllQuestions();

    QuestionListDto getQuestionsByUserIdApproved(String userId);

    Boolean approveQuestionByModerator(String questionId);
    Boolean disapproveQuestionByModerator(String questionId) ;

    Boolean choosingBestAnswer(String questionId, String answerId);

    QuestionListDto getQuestionByOrganiztionId(String organizationId);

    QuestionListDto getAllQuestionForApproval(QuestionIdsDTO questionIdsDTO);

    List<Question> getFeedPaginated(String userId) throws ExecutionException, InterruptedException;

    QuestionListDto getQuestionsByCategory(QuestionIdsDTO questionIdsDTO);
}
