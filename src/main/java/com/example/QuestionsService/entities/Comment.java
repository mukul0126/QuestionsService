package com.example.QuestionsService.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "Comment")
public class Comment {
    @Id
    String commentId;
    String commentBody;
    String parentId;
    String answerId;
    String UserId;
    Date date=new Date();
    String userName;
}
