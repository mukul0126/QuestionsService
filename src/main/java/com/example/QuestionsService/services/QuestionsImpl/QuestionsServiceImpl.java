package com.example.QuestionsService.services.QuestionsImpl;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.responsedto.*;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.repositories.AnswerRepository;
import com.example.QuestionsService.repositories.QuestionRepository;
import com.example.QuestionsService.services.FeignService.UserFeign;
import com.example.QuestionsService.services.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sun.tools.internal.xjc.reader.Ring.add;

@Service
public class QuestionsServiceImpl implements QuestionService {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    private KafkaTemplate<String,Question> kafkaTemplate;
//    private KafkaTemplate<String,NotificationDto> kafkaTemplate1;
    private static final String TOPIC = "questionJson";
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate1;
    private static final String TOPIC1 = "QuoraListener";

    @Override
    public Boolean approveQuestionByModerator(String questionId) {
        Optional<Question> optionalAnswer=questionRepository.findById(questionId);
        Question question=optionalAnswer.get();
        question.setApprovalFlag(true);
       questionRepository.save(question);

        // send notifications
        OrganizationFollowersAndCategoryFollowersDTO organizationFollowersAndCategoryFollowersDTO=userFeign.getOrganizationFollowers(question.getOrgId(),question.getCategoryId());
        System.out.println(organizationFollowersAndCategoryFollowersDTO.getCategoryFollowers());
        System.out.println(organizationFollowersAndCategoryFollowersDTO.getOrganizationFollowers());
        return  true;
    }
    @Override
    public Boolean disapproveQuestionByModerator(String questionId) {
        Optional<Question> optionalAnswer=questionRepository.findById(questionId);
        Question question=optionalAnswer.get();
        question.setApprovalFlag(false);
        questionRepository.save(question);
        return  true;
    }


    @Autowired
    UserFeign userFeign;

    @Override
    public Boolean choosingBestAnswer(String questionId, String answerId) {
        Optional<Question> optionalQuestion=questionRepository.findById(questionId);
        Question question;
        if(optionalQuestion.isPresent()) {
            question = optionalQuestion.get();
            question.setBestAnswerId(answerId);
            questionRepository.save(question);
        }
        Optional<Answer> optionalAnswer=answerRepository.findById(answerId);
        Answer answer;
        String userId;
        if(optionalAnswer.isPresent()) {
           answer = optionalAnswer.get();
          userId = answer.getUserId();
            userFeign.increaseBy5(userId);

        }
   //send notification to owner
        return  true;

    }

    @Override
    public QuestionListDto getQuestionByOrganiztionId(String organizationId) {
        List<Question> list=questionRepository.findAllByOrgId(organizationId);
        QuestionListDto questionListDto=new QuestionListDto();
        questionListDto.setQuestionList(list);
        return  questionListDto;
    }

    @Override
    public Question save(QuestionDto question){
        Question question1=new Question();
        question1.setCategoryId(question.getCategoryId());
        question1.setQuestionBody(question.getQuestionBody());
        question1.setUserId(question.getUserId());
        question1.setUserName(question.getUserName());

//      question1.setTag(question.getTag());
        if(question.getOrgId()!=null){
            question1.setOrgId(question.getOrgId());
            question1.setApprovalFlag(false);
        }


        Question question2= questionRepository.save(question1);


        kafkaTemplate.send(TOPIC, question2);
        if(question.getOrgId()!=null){
            userFeign.addQuestion(question2.getQuestionId(), question.getOrgId());
        }
      userFeign.increaseScoreBy10(question.getUserId());


       // send notifications to followers,category followers,tagged persons

        FollowersAndCategoryFollowingDTO followersAndCategoryFollowingDTO =userFeign.getallFollowers(question2.getUserId(),question2.getCategoryId());
        System.out.println(followersAndCategoryFollowingDTO.getCategoryFollowing());
        System.out.println(followersAndCategoryFollowingDTO.getFollowers());
        NotificationDto notificationDto=new NotificationDto();
        notificationDto.setAppId("quora");
        notificationDto.setTitle(question.getUserName()+"ha posted a question");
        notificationDto.setUserId(followersAndCategoryFollowingDTO.getFollowers());
        try {
            kafkaTemplate1.send(TOPIC1, (new ObjectMapper()).writeValueAsString(notificationDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        kafkaTemplate1.send(TOPIC1, notificationDto);
        //send data
        //send the notif to


        return  question2;
    }


    @Override
    public  String like(String questionid,String userId){
        Optional<Question> questionOptional =questionRepository.findById(questionid);
        Question question;
        if(questionOptional.isPresent())
            question=questionOptional.get();
        else
            return  "id not found";

        if(question.getLikeCount()!=null) {
            List<String> list=question.getLikeUserList();
            List<String> list1=question.getDislikeUserList();
            if(!list.contains(userId) && !list1.contains(userId)) {
                list.add(userId);
                question.setLikeUserList(list);
                question.setLikeCount(question.getLikeCount() + 1);
            }
        }
        else {
            List<String> list1=question.getDislikeUserList();
            if(list1==null) {
                    List<String> list = new ArrayList<String>();
                list.add(userId);
                question.setLikeUserList(list);
                question.setLikeCount(1);
            }
            else{
                if(!list1.contains(userId)){
                    List<String> list = new ArrayList<String>();
                    list.add(userId);
                    question.setLikeUserList(list);
                    question.setLikeCount(1);
                }
            }
        }



        // send data and notification

        List<String> followerslist=userFeign.getOnlyFollowers(question.getUserId());
        System.out.println(followerslist);


        questionRepository.save(question);

        return  question.getQuestionId();
    }
    @Override
    public  String disLike(String questionid,String userId){
        Optional<Question> questionOptional =questionRepository.findById(questionid);
        Question question;
        if(questionOptional.isPresent())
            question=questionOptional.get();
        else
            return  "id not found";

        if(question.getDislikeCount()!=null) {

            List<String> list=question.getDislikeUserList();
            List<String> list1=question.getLikeUserList();
            if(!list.contains(userId) && !list1.contains(userId)) {
                list.add(userId);
                question.setDislikeUserList(list);
                question.setDislikeCount(question.getDislikeCount() + 1);
            }
        }
        else {
            List<String> list1=question.getLikeUserList();
            if(list1==null) {
                List<String> list = new ArrayList<String>();
                list.add(userId);
                question.setDislikeUserList(list);
                question.setDislikeCount(1);
            }
            else{
                if(!list1.contains(userId)){
                    List<String> list = new ArrayList<String>();
                    list.add(userId);
                    question.setDislikeUserList(list);
                    question.setDislikeCount(1);
                }
            }
        }



        // send data and notification
        //owner id par bhi notification
        questionRepository.save(question);

        return  question.getQuestionId();
    }
    @Override
    public Boolean addAnswer(String questionId,String answerId){
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        Question question=questionOptional.get();
        List<String>  list=question.getAnswersList();
        if(list==null)
        {
            list=new ArrayList<String>();
            list.add(answerId);
            question.setAnswersList(list);

        }
        else{
            list.add(answerId);
            question.setAnswersList(list);
        }

        questionRepository.save(question);
return true;

    }

    @Override
    public QuestionListDto getAllQuestions() {
        List<Question> list =  questionRepository.findAll();
        QuestionListDto questionListDto=new QuestionListDto();
        questionListDto.setQuestionList(list);
        return  questionListDto;
    }

    @Override
    public QuestionListDto getQuestionsById(String userid) {
      List<Question> list=  questionRepository.findAllByUserId(userid);
      QuestionListDto questionListDto=new QuestionListDto();
      questionListDto.setQuestionList(list);
      return  questionListDto;

    }

    @Override
    public QuestionListDto getQuestionsByUserIdApproved(String userId) {
        List<Question> list =  questionRepository.findAllByUserId(userId);
        QuestionListDto questionListDto=new QuestionListDto();
        List<Question> finalList= list.stream().filter(Question::getApprovalFlag).collect(Collectors.toList());
//        for(Question question:list){
//            if(question.getApprovalFlag())
//                finalList.add(question);
//        }

        questionListDto.setQuestionList(finalList);
        return  questionListDto;

    }

    @Override
    public List<Question> getFeed(String userId) {
        Set<Question> finalList=new HashSet<Question>();
        FeedDto feedDto=new FeedDto();
       FollowingOrganizationCategoryDTO followingOrganizationCategoryDTO=userFeign.getListOfIds(userId);
     for(String id:  followingOrganizationCategoryDTO.getFollowing()){
         List<Question> list=questionRepository.findAllByUserId(id);
         for(Question question:list){
             finalList.add(question);
         }

     }
        for(String id:  followingOrganizationCategoryDTO.getCategories()){
            List<Question> list=questionRepository.findAllByCategoryId(id);
            for(Question question:list){
                finalList.add(question);
            }
        }
        for(String id:  followingOrganizationCategoryDTO.getOrganization()){
            List<Question> list=questionRepository.findAllByOrgId(id);
            for(Question question:list){
                finalList.add(question);
            }
        }
        List<Question> list3=new ArrayList<>();
        for(Question question:finalList){
            list3.add(question);
        }
//feedDto.setFeed(finalList);
//     return  feedDto;
        return  list3;

    }
}
