package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.entities.Answer;

public interface AnswerService {
    Answer save(AnswerDto answerDto);
    String like(String answerid);
    String disLike(String answerid);

}
