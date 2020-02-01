package com.example.QuestionsService.dtos.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    String commentBody;
    String parentId;
    String answerId;
    String UserId;
    String userName;
}
