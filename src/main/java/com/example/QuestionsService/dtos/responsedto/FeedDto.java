package com.example.QuestionsService.dtos.responsedto;

import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.entities.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FeedDto {
    Set<Question> feed;
}
