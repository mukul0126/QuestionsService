package com.example.QuestionsService.services.FeignService;

import com.example.QuestionsService.dtos.Feign.FollowingOrganizationCategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="UserFeign", url = "http://172.16.20.46:8086/")
public interface UserFeign {

    @RequestMapping(method = RequestMethod.GET, value = "user/getListOfIds/{userId}")
    FollowingOrganizationCategoryDTO getListOfIds(@PathVariable("userId") String userId);


    @RequestMapping(method = RequestMethod.POST, value = "organiaztion/addQuestionToModerator/{questionID}/{organizationId}")
    boolean addQuestion(@PathVariable("questionID") String questionID, @PathVariable("organizationId") String organizationId);

    @RequestMapping(method = RequestMethod.POST, value = "organiaztion/addAnswerToModerator/{answerID}/{organizationId}")
    boolean addAnswer(@PathVariable("answerID") String answerID, @PathVariable("organizationId") String organizationId);

    @RequestMapping(method = RequestMethod.POST, value = "user/increaseScoreBy10/{userId}")
    boolean increaseScoreBy10(@PathVariable("userId") String userId);

    @RequestMapping(method = RequestMethod.POST, value = "user/increaseScoreBy1/{userId}")
    boolean increaseScoreBy1(@PathVariable("userId") String userId);

    @RequestMapping(method = RequestMethod.POST, value = "user/decreaseBy1/{userId}")
    boolean decreaseBy1(@PathVariable("userId") String userId);

    @RequestMapping(method = RequestMethod.POST, value = "user/increaseBy5/{userId}")
    boolean increaseBy5(@PathVariable("userId") String userId);
}
