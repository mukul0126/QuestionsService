package com.example.QuestionsService.services.QuestionsImpl;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import com.example.QuestionsService.dtos.requestdto.QuestionDto;
import com.example.QuestionsService.dtos.requestdto.QuestionIdsDTO;
import com.example.QuestionsService.dtos.requestdto.TagsDTO;
import com.example.QuestionsService.dtos.responsedto.*;
import com.example.QuestionsService.entities.Answer;
import com.example.QuestionsService.entities.Question;
import com.example.QuestionsService.repositories.AnswerRepository;
import com.example.QuestionsService.repositories.QuestionRepository;
import com.example.QuestionsService.services.FeignService.UserFeign;
import com.example.QuestionsService.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
public class QuestionsServiceImpl implements QuestionService {
    public static final String QUORA = "quora";
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    private KafkaTemplate<String, Question> kafkaTemplate;


//    private KafkaTemplate<String,NotificationDto> kafkaTemplate1;
    private static final String TOPIC = "questionJson";
    @Autowired
    KafkaTemplate<String, NotificationDto> kafkaTemplate1;
    private static final String TOPIC1 = "QuoraListener1";
    @Autowired
    KafkaTemplate<String, CRMDto> kafkaTemplate2;
    private static final String TOPIC2 = "CRMListener";

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
        List<String> taglist=question.getPersonsTag();
        TagsDTO tagsDTO=new TagsDTO();
        tagsDTO.setListTags(taglist);

       FollowersAndCatogoriesAndTagsDTO followersAndCategoryFollowingDTO =userFeign.getALlFollowersAndCategoriesAndTags(tagsDTO,question2.getUserId(),question2.getCategoryId());
        System.out.println(followersAndCategoryFollowingDTO.getCategoryFollowers());
        System.out.println(followersAndCategoryFollowingDTO.getFollowers());
        System.out.println(followersAndCategoryFollowingDTO.getTags());
        NotificationDto notificationDto=new NotificationDto();
        notificationDto.setTitle("QUESTION POSTED");
        notificationDto.setMessage(question.getUserName()+" posted a new question");
        notificationDto.setUserId(followersAndCategoryFollowingDTO.getFollowers());

            kafkaTemplate1.send(TOPIC1, notificationDto);
           NotificationDto notificationDto1=new NotificationDto();
           notificationDto1.setTitle("QUESTION POSTED");
           notificationDto1.setUserId(followersAndCategoryFollowingDTO.getCategoryFollowers());
           notificationDto1.setMessage("New Question posted in "+question.getCategoryId()+"category");

            kafkaTemplate1.send(TOPIC1, notificationDto1);
        NotificationDto notificationDto2=new NotificationDto();
        notificationDto2.setTitle("QUESTION POSTED");
        notificationDto2.setUserId(followersAndCategoryFollowingDTO.getTags());
        notificationDto2.setMessage(question.getUserName()+" has asked you a Question");
        kafkaTemplate1.send(TOPIC1, notificationDto2);
        //send data
        //send the notif to tagged


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
            if(!list.contains(userId) && (list1==null || !list1.contains(userId))) {
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

        questionRepository.save(question);

        // send data and notification

        List<String> followerslist=userFeign.getOnlyFollowers(question.getUserId());
        System.out.println(followerslist);

        NotificationDto notificationDto1=new NotificationDto();
        notificationDto1.setTitle("QUESTION LIKED");
        notificationDto1.setUserId(followerslist);
        notificationDto1.setMessage(question.getUserName()+"got a like on his question");
        kafkaTemplate1.send(TOPIC1, notificationDto1);
        NotificationDto notificationDto2=new NotificationDto();
        notificationDto2.setTitle("QUESTION LIKED");

        List<String> list=new ArrayList<>();
        list.add(question.getUserId());
        notificationDto2.setUserId(list);
        notificationDto1.setMessage("You got a like on your Question");
        kafkaTemplate1.send(TOPIC1, notificationDto2);


        return  question.getQuestionId();
    }

    @Override
    public QuestionListDto getAllQuestionForApproval(QuestionIdsDTO questionIdsDTO) {
//        System.out.println(questionList);
        List<Question> questions=new ArrayList<>();
        List<String> questionList=questionIdsDTO.getListOfQuestionId();
if(questionList!=null && questionList.size()!=0) {
    for (String id : questionList) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        Question question = null;
        if (questionOptional.isPresent())
            question = questionOptional.get();

        questions.add(question);

    }
}
        QuestionListDto questionListDto=new QuestionListDto();
        questionListDto.setQuestionList(questions);
        return questionListDto;
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
            if(!list.contains(userId) && (list1==null || !list1.contains(userId))) {
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

        CRMDto crmDto=new CRMDto();
        crmDto.setLeadId(userId);
        crmDto.setPostPersonId(question.getUserId());
        crmDto.setDescriptionOfPost(question.getQuestionBody());
        crmDto.setTicketId(question.getQuestionId());
        kafkaTemplate2.send(TOPIC2,crmDto);

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
    public QuestionListDto getQuestionsByCategory(QuestionIdsDTO questionIdsDTO) {
        QuestionListDto questionListDto=new QuestionListDto();
        List<Question> list=new ArrayList<>();
        for(String id: questionIdsDTO.getListOfQuestionId()){
            List<Question> questionList=questionRepository.findAllByCategoryId(id);
            list.addAll(questionList);
        }

        questionListDto.setQuestionList(list);
        return      questionListDto;
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
    public List<Question> getFeedPaginated(String userId) throws ExecutionException, InterruptedException {
        Set<Question> finalList=new HashSet<Question>();
        FeedDto feedDto=new FeedDto();
        FollowingOrganizationCategoryDTO followingOrganizationCategoryDTO=userFeign.getListOfIds(userId);
        CompletableFuture<Set<Question>> process1 = CompletableFuture.supplyAsync(() -> {
            Set<Question> chunk1 = new HashSet<>();
            List<String> followinglist = followingOrganizationCategoryDTO.getFollowing();
            if (followinglist != null) {
                for (String id : followinglist) {
                    List<Question> list = questionRepository.findAllByUserId(id);
                    chunk1.addAll(list);
                }
            }
            return chunk1;
        });
        CompletableFuture<Set<Question >> process2 = CompletableFuture.supplyAsync(() -> {
            Set<Question> chunk2 = new HashSet<>();
            List<String> categorylist = followingOrganizationCategoryDTO.getCategories();
            if (categorylist != null) {
                for (String id : categorylist) {
                    List<Question> list = questionRepository.findAllByCategoryId(id);
                   chunk2.addAll(list);
                }
            }
           return chunk2;
        });
        CompletableFuture<Set<Question>> process3 = CompletableFuture.supplyAsync(() -> {
            Set<Question> chunk3 = new HashSet<>();
            List<String> organizationlist=followingOrganizationCategoryDTO.getOrganization();
            if(organizationlist!=null) {
                for (String id : organizationlist) {
                    List<Question> list = questionRepository.findAllByOrgId(id);
                   chunk3.addAll(list);
                }
            }
            return chunk3;
        });
        Set<Question> fl = process1.thenCombineAsync(process2, this::combine).thenCombineAsync(process3, this::combine).get();
        List<Question> list3=new ArrayList<>();
        for(Question question:fl){
            list3.add(question);
        }

        return  list3;

    }


    private Set<Question> combine(Set<Question> questions1, Set<Question> questions2) {
        questions1.addAll(questions2);
        return questions1;
    }

    @Override
    public List<Question> getFeed(String userId) {
        Set<Question> finalList=new HashSet<Question>();
        FeedDto feedDto=new FeedDto();
       FollowingOrganizationCategoryDTO followingOrganizationCategoryDTO=userFeign.getListOfIds(userId);
       List<String> followinglist=followingOrganizationCategoryDTO.getFollowing();
       if(followinglist!=null) {
           for (String id : followinglist) {
               List<Question> list = questionRepository.findAllByUserId(id);
               for (Question question : list) {
                   finalList.add(question);
               }

           }
       }
       List<String> categorylist=followingOrganizationCategoryDTO.getCategories();
       if(categorylist!=null) {
           for (String id : categorylist) {
               List<Question> list = questionRepository.findAllByCategoryId(id);
               for (Question question : list) {
                   finalList.add(question);
               }
           }
       }

        List<String> organizationlist=followingOrganizationCategoryDTO.getOrganization();
       if(organizationlist!=null) {
           for (String id : organizationlist) {
               List<Question> list = questionRepository.findAllByOrgId(id);
               for (Question question : list) {
                   finalList.add(question);
               }
           }
       }
        List<Question> list3=new ArrayList<>();
        for(Question question:finalList){
            list3.add(question);
        }

        return  list3;

    }
}
