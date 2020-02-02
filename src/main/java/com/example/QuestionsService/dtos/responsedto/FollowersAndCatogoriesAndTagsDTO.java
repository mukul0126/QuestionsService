package com.example.QuestionsService.dtos.responsedto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class FollowersAndCatogoriesAndTagsDTO {
    List<String> followers;
    List<String> categoryFollowers;
    List<String> tags;
}
