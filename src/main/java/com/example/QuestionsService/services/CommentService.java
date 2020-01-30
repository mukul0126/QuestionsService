package com.example.QuestionsService.services;

import com.example.QuestionsService.dtos.responsedto.CommentListDto;
import com.example.QuestionsService.entities.Comment;

public interface CommentService {
    Comment save(Comment comment);

    CommentListDto getcommentbyanswer(String answerId);

    CommentListDto getcommentbyparent(String parentId);
}
