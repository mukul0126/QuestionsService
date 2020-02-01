package com.example.QuestionsService.dtos.requestdto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnswerDto {

    String  questionId;
    String  userId;
    String  answerBody;
    String orgId;
    String userName;

}
