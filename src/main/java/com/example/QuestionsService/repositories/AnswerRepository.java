package com.example.QuestionsService.repositories;

import com.example.QuestionsService.entities.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends MongoRepository<Answer,String> {
     List<Answer> findAllByUserId(String userId);
     List<Answer> findAllByQuestionId(String QuestionId);


}
