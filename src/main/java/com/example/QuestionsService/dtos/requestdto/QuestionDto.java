package com.example.QuestionsService.dtos.requestdto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class QuestionDto {

    String userId;
    String categoryId;
    String questionBody;
//    String tag;
    List<String> personsTag;
    String orgId;
}
