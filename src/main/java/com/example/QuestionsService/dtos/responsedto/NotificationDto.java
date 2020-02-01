package com.example.QuestionsService.dtos.responsedto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class NotificationDto {
    String title;
    List<String> userId;
    String appId;
}
