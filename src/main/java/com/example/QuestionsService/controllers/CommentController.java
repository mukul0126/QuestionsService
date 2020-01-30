package com.example.QuestionsService.controllers;

import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.dtos.requestdto.CommentDto;
import com.example.QuestionsService.dtos.responsedto.CommentListDto;
import com.example.QuestionsService.entities.Comment;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.services.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*",allowedHeaders = "*")

public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping(value = "/addcomment")
    public ResponseEntity<String> addcomment(@RequestBody CommentDto commentDto) {

      Comment comment =new Comment();
        BeanUtils.copyProperties(commentDto,comment);
        Comment comment1=commentService.save(comment);
        return new ResponseEntity<String>(comment1.getCommentId(),HttpStatus.CREATED);

    }

    @GetMapping(value = "/getcommentbyanswer/{answerId}")
    public CommentListDto getcommentbyanswer(@PathVariable("answerId") String answerId){
        return  commentService.getcommentbyanswer(answerId);
    }
    @GetMapping(value = "/getcommentbyparent/{parentId}")
    public CommentListDto getcommentbyparent(@PathVariable("parentId") String parentId){
        return  commentService.getcommentbyparent(parentId);
    }
}
