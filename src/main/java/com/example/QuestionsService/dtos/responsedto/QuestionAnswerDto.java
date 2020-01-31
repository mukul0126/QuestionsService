package com.example.QuestionsService.dtos.responsedto;

import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class QuestionAnswerDto {
    Question question;
    List<Answer> answerList;
}
