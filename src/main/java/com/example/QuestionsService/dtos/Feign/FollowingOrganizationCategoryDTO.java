package com.example.QuestionsService.dtos.Feign;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FollowingOrganizationCategoryDTO {
    List<String> following;
    List<String> categories;
    List<String> organization;
}
