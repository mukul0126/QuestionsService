package com.example.QuestionsService.services.Answersimpl;

import com.example.QuestionsService.dtos.requestdto.AnswerDto;
import com.example.QuestionsService.dtos.requestdto.AnswerIdsDTO;
import com.example.QuestionsService.dtos.responsedto.*;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.repositories.AnswerRepository;
import com.example.QuestionsService.repositories.QuestionRepository;
import com.example.QuestionsService.services.AnswerService;
import com.example.QuestionsService.services.FeignService.UserFeign;
import com.example.QuestionsService.services.QuestionService;
import com.example.QuestionsService.services.QuestionsImpl.QuestionsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.QuestionsService.services.QuestionsImpl.QuestionsServiceImpl.QUORA;

@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserFeign userFeign;
    @Autowired
    KafkaTemplate<String, NotificationDto> kafkaTemplate1;
    private static final String TOPIC1 = "QuoraListener";
    @Override
    public Answer save(AnswerDto answerDto){
        Answer answer=new Answer();
        answer.setAnswerBody(answerDto.getAnswerBody());
        answer.setQuestionId(answerDto.getQuestionId());
        answer.setUserId(answerDto.getUserId());
        answer.setUserName(answerDto.getUserName());
        if(answerDto.getOrgId()!=null){
            answer.setOrgId(answerDto.getOrgId());
            answer.setApprovalFlag(false);
        }

        Answer answer1=   answerRepository.save(answer);
        String questionid=answer1.getQuestionId();
        Optional<Question> questionOptional=questionRepository.findById(questionid);
        Question question=questionOptional.get();

         questionService.addAnswer(answer1.getQuestionId(),answer1.getAnswerId());

        if(answerDto.getOrgId()!=null) {
            userFeign.addAnswer(answer1.getAnswerId(),answer1.getOrgId());
        }

        //send notification
        FollowersAndCategoryFollowingDTO followersAndCategoryFollowingDTO =userFeign.getallFollowers(question.getUserId(),question.getCategoryId());
        System.out.println(followersAndCategoryFollowingDTO.getCategoryFollowing());
        System.out.println(followersAndCategoryFollowingDTO.getFollowers());
        //get owner id of answer
        NotificationDto notificationDto=new NotificationDto();
        notificationDto.setTitle("Answer Posted");
        notificationDto.setMessage(question.getUserName()+" Got Answer on his post");
        notificationDto.setUserId(followersAndCategoryFollowingDTO.getFollowers());
        kafkaTemplate1.send(TOPIC1, notificationDto);
        NotificationDto notificationDto1=new NotificationDto();
        notificationDto1.setTitle("Answer Posted");
        notificationDto1.setUserId(followersAndCategoryFollowingDTO.getCategoryFollowing());
        notificationDto1.setMessage(" Question of  "+question.getCategoryId()+"category" +" got an Answer");
        kafkaTemplate1.send(TOPIC1, notificationDto1);
        NotificationDto notificationDto2=new NotificationDto();
        notificationDto2.setTitle("Answer Posted");
        List<String> list=new ArrayList<>();
        list.add(question.getUserId());
        notificationDto2.setUserId(list);
        notificationDto1.setMessage("You got a Answer on your Question");
        kafkaTemplate1.send(TOPIC1, notificationDto2);

        return  answer1;
    }

    @Override
    public AnswerListDto getAllAnswerForApproval(AnswerIdsDTO answerList) {
        List<Answer> answers=new ArrayList<>();
        List<String> answerlist=answerList.getListOfAnswer();
        for(String id:answerlist){
            Optional<Answer> answerOptional =answerRepository.findById(id);
            Answer answer=null;
            if(answerOptional.isPresent())
                answer=answerOptional.get();

            answers.add(answer);

        }
       AnswerListDto answerListDto=new AnswerListDto();
      answerListDto.setListOfAnswerIds(answers);
        return answerListDto;
    }

    @Override
    public QuestionAnswerDto getAnswersByQuestionId(String questionId) {
        Optional<Question> optionalQuestion=questionRepository.findById(questionId);
        Question question=new Question();
        if(optionalQuestion.isPresent())
         question=optionalQuestion.get();
        QuestionAnswerDto questionAnswerDto=new QuestionAnswerDto();
        questionAnswerDto.setQuestion(question);

        List<Answer> list=answerRepository.findAllByQuestionId(questionId);
            List<Answer> list2=new ArrayList<>();
            if(list!=null) {
                for (Answer answer : list) {
                    if (answer.getApprovalFlag() == true)
                        list2.add(answer);
                }
            }
        questionAnswerDto.setAnswerList(list2);
        return  questionAnswerDto;
    }

    @Override
    public  String like(String answerid,String userId){
        Optional<Answer> answerOptional =answerRepository.findById(answerid);

       Answer answer;
        if(answerOptional.isPresent())
          answer  =answerOptional.get();
        else
            return  "id not found";

        if(answer.getLikeCount()!=null) {
            List<String> list=answer.getLikeUserList();
            List<String> list1= answer.getDislikeUserList();
            if(!list.contains(userId) && (list1==null || !list1.contains(userId))) {
                list.add(userId);
                answer.setLikeUserList(list);
                answer.setLikeCount(answer.getLikeCount() + 1);
            }
        }
        else {
            List<String> list1=answer.getDislikeUserList();
            if(list1==null) {
                List<String> list = new ArrayList<String>();
                list.add(userId);
                answer.setLikeUserList(list);
                answer.setLikeCount(1);
            }
            else{
                if(!list1.contains(userId)){
                    List<String> list = new ArrayList<String>();
                    list.add(userId);
                    answer.setLikeUserList(list);
                    answer.setLikeCount(1);
                }
            }
        }



        // send data and notification

      Answer answer1=  answerRepository.save(answer);
        userFeign.increaseScoreBy1(answer.getUserId());

        String questionid=answer1.getQuestionId();
        Optional<Question> questionOptional=questionRepository.findById(questionid);
        Question question=questionOptional.get();

        List<String> followerslist=userFeign.getOnlyFollowers(question.getUserId());
        System.out.println(followerslist);
        return  answer.getAnswerId();
    }

    @Override
    public List<Answer> getAnswersByUserId(String userId) {
        return  answerRepository.findAllByUserId(userId);
    }

    @Override
    public Boolean approveAnswerByModerator(String answerId) {
       Optional<Answer> optionalAnswer=answerRepository.findById(answerId);
       Answer answer=optionalAnswer.get();
       answer.setApprovalFlag(true);
       answerRepository.save(answer);

       // send notifications
       return  true;
    }

    @Override
    public Boolean disapproveAnswerByModerator(String answerId) {

            Optional<Answer> optionalAnswer=answerRepository.findById(answerId);
            Answer answer=optionalAnswer.get();
            answer.setApprovalFlag(false);
            answerRepository.save(answer);
            return  true;
    }

    @Override
    public  String disLike(String answerid,String userId){
        Optional<Answer> answerOptional =answerRepository.findById(answerid);
        Answer answer;
        if(answerOptional.isPresent())
            answer=answerOptional.get();
        else
            return  "id not found";

        if(answer.getDislikeCount()!=null) {
            List<String> list=answer.getDislikeUserList();
            List<String> list1=answer.getLikeUserList();
            if(!list.contains(userId) && (list1==null || !list1.contains(userId))) {
                list.add(userId);
                answer.setDislikeUserList(list);
                answer.setDislikeCount(answer.getDislikeCount() + 1);
            }
        }
        else {
            List<String> list=new ArrayList<String>();
            list.add(userId);
            answer.setDislikeUserList(list);
            answer.setDislikeCount(1);
        }



        // send data and notification

        answerRepository.save(answer);
        userFeign.decreaseBy1(answer.getUserId());
        return  answer.getAnswerId();
    }

}
