package com.example.QuestionsService.dtos.responsedto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class OrganizationFollowersAndCategoryFollowersDTO {
    List<String> organizationFollowers;
    List<String> categoryFollowers;
}
