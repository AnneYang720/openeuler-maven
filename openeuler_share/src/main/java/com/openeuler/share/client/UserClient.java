package com.openeuler.share.client;

import com.openeuler.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("openeuler-user")
public interface UserClient {
    @GetMapping("/user/loginname")
    User findByLoginName(@RequestBody User user);

    @GetMapping( "/user/find/byid")
    User findUserById(@RequestBody String id);
}
