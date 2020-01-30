package com.example.QuestionsService.dtos.responsedto;

import com.example.QuestionsService.entities.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class CommentListDto {
    List<Comment> commentList;
}
