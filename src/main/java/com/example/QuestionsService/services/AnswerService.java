package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.dtos.responsedto.QuestionAnswerDto;
import com.example.QuestionsService.entities.Answer;

import java.util.List;

public interface AnswerService {
    Answer save(AnswerDto answerDto);
    String like(String answerid,String userId);
    String disLike(String answerid,String userId);

    List<Answer> getAnswersByUserId(String userId);

    QuestionAnswerDto getAnswersByQuestionId(String questionId);

    Boolean approveAnswerByModerator(String answerId);

    Boolean disapproveAnswerByModerator(String answerId);
}
