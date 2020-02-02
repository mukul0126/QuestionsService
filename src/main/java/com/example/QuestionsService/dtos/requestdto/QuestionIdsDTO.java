package com.example.QuestionsService.dtos.requestdto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class QuestionIdsDTO {
    List<String> listOfQuestionId;

    @Override
    public String toString() {
        return "QuestionIdsDTO{" +
                "listOfQuestionId=" + listOfQuestionId +
                '}';
    }
}
