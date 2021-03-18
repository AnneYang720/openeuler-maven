package com.openeuler.share.client;

import com.openeuler.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value="openeuler-user")
public interface UserClient {
    @RequestMapping(value = "/user/find/loginname", method = RequestMethod.POST)
    User findByLoginName(@RequestBody User user);

    @RequestMapping( value ="/user/find/id", method = RequestMethod.POST)
    User findUserById(@RequestBody String id);

    @RequestMapping( value ="/user/addshare", method = RequestMethod.POST)
    String addShareUser();

    @RequestMapping( value ="/user/deleterepouser/id", method = RequestMethod.DELETE)
    String deleteRepoUser(@RequestBody String id);
}
