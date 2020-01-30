package com.example.QuestionsService.repositories;

import com.example.QuestionsService.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends MongoRepository<Comment,String> {
List<Comment> findAllByAnswerId(String AnswerId);
List<Comment> findAllByParentId(String parentId);

}
