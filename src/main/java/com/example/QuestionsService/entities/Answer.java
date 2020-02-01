package com.example.QuestionsService.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "Answer")
public class Answer {
    @Id
    String  answerId;
    String  questionId;
    String  userId;
    Integer  likeCount;
    List<String> likeUserList;
    Integer  dislikeCount;
    List<String> dislikeUserList;
    String  answerBody;
    Date date=new Date();
    Boolean  approvalFlag=true;
    List<String> commentsList;
    String orgId;
    String userName;
}
