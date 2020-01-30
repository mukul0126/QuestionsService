package com.example.QuestionsService.services.CommentImpl;

import com.example.QuestionsService.dtos.responsedto.CommentListDto;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Comment;
import com.example.QuestionsService.repositories.AnswerRepository;
import com.example.QuestionsService.repositories.CommentRepository;
import com.example.QuestionsService.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CommentServiceIm implements CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    AnswerRepository answerRepository;

    @Override
    public CommentListDto getcommentbyparent(String parentId) {
        List<Comment> list=commentRepository.findAllByParentId(parentId);
        CommentListDto commentListDto=new CommentListDto();
        commentListDto.setCommentList(list);
        return  commentListDto;
    }

    @Override
    public Comment save(Comment comment) {

        Comment comment1= commentRepository.save(comment);

        if(comment1.getParentId()==null)
        {
            Optional<Answer> optionalAnswer =answerRepository.findById(comment1.getAnswerId());
            Answer answer=optionalAnswer.get();
            List<String> list=answer.getCommentsList();
            if(list==null){
                List<String> list1=new ArrayList<>();
                list1.add(comment1.getCommentId());
                answer.setCommentsList(list1);
                answerRepository.save(answer);
            }
            else{
                list.add(comment1.getCommentId());
                answer.setCommentsList(list);
                answerRepository.save(answer);
            }


        }
        return  comment1;

    }

    @Override
    public CommentListDto getcommentbyanswer(String answerId) {
        List<Comment> list=commentRepository.findAllByAnswerId(answerId);
        List<Comment> list1=new ArrayList<>();
       for(Comment comment:list){
           if(comment.getParentId()==null)
               list1.add(comment);
       }
        CommentListDto commentListDto=new CommentListDto();
        commentListDto.setCommentList(list1);
        return  commentListDto;
    }
}
