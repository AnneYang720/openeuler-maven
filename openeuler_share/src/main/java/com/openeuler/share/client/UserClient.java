package com.openeuler.share.client;

import com.openeuler.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value="openeuler-user")
public interface UserClient {
    @RequestMapping(value = "/user/find/loginname", method = RequestMethod.POST)
    User findByLoginName(@RequestBody User user);

    @RequestMapping( value ="/user/find/byid", method = RequestMethod.POST)
    User findUserById(@RequestBody String id);
}
