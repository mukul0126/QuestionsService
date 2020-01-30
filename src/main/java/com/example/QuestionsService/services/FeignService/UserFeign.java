package com.example.QuestionsService.services.FeignService;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="UserFeign", url = "http://172.16.20.46:8086/")
public interface UserFeign {

    @RequestMapping(method = RequestMethod.GET , value = "user/getListOfIds/{userId}")
    FollowingOrganizationCategoryDTO getListOfIds(@PathVariable("userId") String userId);


    @RequestMapping(method = RequestMethod.POST , value = "organiaztion/addQuestionToModerator/{questionID}/{organizationId}")
     boolean addQuestion(@PathVariable("questionID") String questionID,@PathVariable("organizationId") String organizationId);


}
