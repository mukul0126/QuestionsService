package com.example.QuestionsService.services.Answersimpl;

import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.repositories.AnswerRepository;
import com.example.QuestionsService.services.AnswerService;
import com.example.QuestionsService.services.QuestionService;
import com.example.QuestionsService.services.QuestionsImpl.QuestionsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    QuestionService questionService;
    @Override
    public Answer save(AnswerDto answerDto){
        Answer answer=new Answer();
        answer.setAnswerBody(answerDto.getAnswerBody());
        answer.setQuestionId(answerDto.getQuestionId());
        answer.setUserId(answerDto.getUserId());

        if(answerDto.getOrgId()!=null){
            answer.setOrgId(answerDto.getOrgId());
            answer.setApprovalFlag(false);
        }

        Answer answer1=   answerRepository.save(answer);
         questionService.addAnswer(answer1.getQuestionId(),answer1.getAnswerId());
        //feign client send ansid and orgid
        //send notification
        return  answer1;
    }
    @Override
    public  String like(String answerid){
        Optional<Answer> answerOptional =answerRepository.findById(answerid);
       Answer answer;
        if(answerOptional.isPresent())
          answer  =answerOptional.get();
        else
            return  "id not found";

        if(answer.getLikeCount()!=null)
            answer.setLikeCount(answer.getLikeCount()+1);
        else
            answer.setLikeCount(1);

        answerRepository.save(answer);
        return  answer.getAnswerId();
    }
    @Override
    public  String disLike(String answerid){
        Optional<Answer> answerOptional =answerRepository.findById(answerid);
        Answer answer;
        if(answerOptional.isPresent())
            answer=answerOptional.get();
        else
            return  "id not found";

        if(answer.getDislikeCount()!=null)
            answer.setDislikeCount(answer.getDislikeCount()+1);
        else
            answer.setDislikeCount(1);

        answerRepository.save(answer);
        return  answer.getAnswerId();
    }

}
