package com.openeuler.user.controller;

import com.openeuler.user.pojo.RepoUser;
import com.openeuler.user.pojo.User;
import com.openeuler.user.service.UserService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器层
 * @author AnneY
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        user = userService.login(user);
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "登录失败");
        }
        // 登录成功后的操作
        String token = jwtUtil.createJWT(user.getId(), user.getEmail(), "user");
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return new Result(true, StatusCode.OK, "登录成功", map);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        List<User> userList = userService.checkUser(user);
        if (userList == null || userList.isEmpty()) {
            userService.register(user);
            return new Result(true, StatusCode.OK, "注册成功");
        } else {
            return new Result(false, StatusCode.ERROR, "注册失败，邮箱或用户名已经被使用");
        }
    }

    /**
     * 当前用户获得本人信息
     *
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result getCurInfo(@RequestHeader(value="X-User-Id") String userId) {
        return new Result(true, StatusCode.OK, "查询成功", userService.getCurInfo(userId));
    }

    /**
     * 当前登陆用户修改个人信息
     * @param user
     */
    @RequestMapping(value="/saveinfo", method= RequestMethod.PUT)
    public Result updateInfo(@RequestBody User user, @RequestHeader(value="X-User-Id") String userId){
        // user.setId(parseToken(token))
        userService.update(user,userId);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据UserID查询并返回用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/find/id", method = RequestMethod.POST)
    public User findUserById(@RequestBody String id) {
        return userService.findById(id);
    }

    /**
     * 当前用户增加分享，本人release仓库增加用户名和密码
     * @return
     */
    @RequestMapping(value = "/addshare", method = RequestMethod.POST)
    public String addShareUser(@RequestBody String id) {
        return userService.addRepoUser( id,"release");
    }

    /**
     * 当前用户删除分享，本人release仓库删除相应的用户名和密码
     * @return
     */
    @RequestMapping(value = "/deleterepouser", method = RequestMethod.DELETE)
    public void deleteRepoUser(@RequestBody String id) {
        userService.deleteRepoUser(id);
    }

    /**
     * 根据RepoUserId返回该条RepoUser数据
     * @return
     */
    @RequestMapping(value = "/getrepoinfo/id", method = RequestMethod.POST)
    public RepoUser getRepoInfo(@RequestBody String id) {
        return userService.getRepoInfo(id);
    }

    /**
     * 返回当前用户本人仓库的RepoUser数据
     * @return
     */
    @RequestMapping(value = "/{repo}/getrepouserinfo", method = RequestMethod.GET)
    public Result getRepoInfoByRepo(@PathVariable String repo, @RequestHeader(value="X-User-Id") String userId) {
        User curUser = userService.findById(userId);
        System.out.println(repo);
        String repoUserId = repo.equals("release")? curUser.getRepoUserReleaseId():curUser.getRepoUserSnapshotId();
        return new Result(true, StatusCode.OK, "查询成功", userService.getRepoInfo(repoUserId));
    }

    /**
     * 根据UserName查询用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/find/loginname", method = RequestMethod.POST)
    public User findByLoginName(@RequestBody User user) {
        System.out.println("findByLoginName: "+user.getLoginName());
        return userService.findByLoginName(user.getLoginName());
    }

}