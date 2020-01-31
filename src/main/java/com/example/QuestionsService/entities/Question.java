package com.example.QuestionsService.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "Question")
public class Question {
    @Id
    String questionId;
    String userId;
    Integer likeCount;
    List<String> likeUserList;
    Integer dislikeCount;
    List<String> dislikeUserList;
    List<String> answersList;
    String categoryId;
//    String tag;
    String questionBody;
    Date date=new Date();
    Boolean approvalFlag=true;
    String bestAnswerId;
    String orgId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionId, question.questionId) &&
                Objects.equals(userId, question.userId) &&
                Objects.equals(likeCount, question.likeCount) &&
                Objects.equals(dislikeCount, question.dislikeCount) &&
                Objects.equals(answersList, question.answersList) &&
                Objects.equals(categoryId, question.categoryId) &&
                Objects.equals(questionBody, question.questionBody) &&
                Objects.equals(date, question.date) &&
                Objects.equals(approvalFlag, question.approvalFlag) &&
                Objects.equals(bestAnswerId, question.bestAnswerId) &&
                Objects.equals(orgId, question.orgId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(questionId, userId, likeCount, dislikeCount, answersList, categoryId, questionBody, date, approvalFlag, bestAnswerId, orgId);
    }
}
