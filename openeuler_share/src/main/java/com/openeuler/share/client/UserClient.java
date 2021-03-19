package com.openeuler.share.client;

import com.openeuler.user.pojo.RepoUser;
import com.openeuler.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value="openeuler-user")
public interface UserClient {
    @RequestMapping(value = "/user/find/loginname", method = RequestMethod.POST)
    User findByLoginName(@RequestBody User user);

    @RequestMapping( value ="/user/find/id", method = RequestMethod.POST)
    User findUserById(@RequestBody String id);

    @RequestMapping( value ="/user/addshare", method = RequestMethod.POST)
    String addShareUser(@RequestBody String id);

    @RequestMapping( value ="/user/deleterepouser", method = RequestMethod.DELETE)
    String deleteRepoUser(@RequestBody String id);

    @RequestMapping( value ="/user/getrepoinfo/id", method = RequestMethod.POST)
    RepoUser getRepoInfo(@RequestBody String id);
}
