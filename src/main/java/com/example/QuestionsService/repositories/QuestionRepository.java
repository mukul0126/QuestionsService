package com.example.QuestionsService.repositories;

import com.example.QuestionsService.entities.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question,String> {

    List<Question> findAllByCategoryId(String categoryid);
    List<Question> findAllByOrgId(String orgid);
    List<Question>  findAllByUserId(String userId);
}
