package com.example.QuestionsService.dtos.responsedto;

import com.example.QuestionsService.entities.Answer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AnswerListDto {
    List<Answer> listOfAnswerIds;
}
