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
 * @author Administrator
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
     * 查询全部数据
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        return new Result(true, StatusCode.OK, "查询成功", userService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findById(id));
    }

    /**
     * 根据ID查询返回用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/find/id", method = RequestMethod.POST)
    public User findUserById(@RequestBody String id) {
        return userService.findById(id);
    }

    /**
     * 当前登录用户给本人release仓库增加用户名和密码
     * @return
     */
    @RequestMapping(value = "/addshare", method = RequestMethod.POST)
    public String addShareUser(@RequestBody String id) {
        return userService.addRepoUser( id,"release");
    }

    /**
     * 当前登录用户给本人release仓库删除一项用户名和密码
     * @return
     */
    @RequestMapping(value = "/deleterepouser", method = RequestMethod.DELETE)
    public void deleteRepoUser(@RequestBody String id) {
        userService.deleteRepoUser(id);
    }

    /**
     * 返回某个repoUser的用户名和密码
     * @return
     */
    @RequestMapping(value = "/getrepoinfo/id", method = RequestMethod.POST)
    public RepoUser getRepoInfo(@RequestBody String id) {
        return userService.getRepoInfo(id);
    }

    /**
     * 返回当前用户本人仓库的repoUser的用户名和密码
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
     * 根据UserName查询
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/find/loginname", method = RequestMethod.POST)
    public User findByLoginName(@RequestBody User user) {
        System.out.println("findByLoginName: "+user.getLoginName());
        return userService.findByLoginName(user.getLoginName());
    }

    /**
     * 根据UserName查询
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/find/loginname/bystring", method = RequestMethod.POST)
    public User findByLoginNameByString(@RequestBody String userName) {
        return userService.findByLoginName(userName);
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findSearch(searchMap));
    }

    /**
     * 管理员修改
     * @param user
     */
    @RequestMapping(value="/{id}", method= RequestMethod.PUT)
    public Result update(@RequestBody User user,  @PathVariable String id) {
        userService.updateById(user, id);
        return new Result(true, StatusCode.OK, "修改成功");
    }



    /**
     * 删除
     * @param id
     */
    @RequestMapping(value="/{id}", method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id ){
        userService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}