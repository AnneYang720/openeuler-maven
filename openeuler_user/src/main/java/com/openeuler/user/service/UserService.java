package com.openeuler.user.service;

import com.openeuler.user.dao.RepoUserDao;
import com.openeuler.user.dao.UserDao;
import com.openeuler.user.pojo.RepoUser;
import com.openeuler.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.List;
import java.util.*;

/**
 * 用户微服务的服务层
 *
 * @author AnneY
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RepoUserDao repoUserDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Value("${amazon.s3.url}")
    private String url;


    /**
     * 检查用户的邮箱或用户名是否被注册
     *
     * @param user
     * @return
     */
    public List<User> checkUser(User user) {
        return userDao.findByLoginNameOrEmail(user.getLoginName(), user.getEmail());
    }


    /**
     * 获得当前用户信息
     *
     */
    public User getCurInfo(String userId) {
        User currUser = userDao.findById(userId).get();
        currUser.setId("");
        currUser.setPassword("");
        return currUser;
    }


    /**
     * 根据UserId查询User实体
     *
     * @param id
     * @return
     */
    public User findById(String id) {
        return userDao.findById(id).get();
    }


    /**
     * 根据用户名LoginName查询User实体
     *
     * @param loginName
     * @return
     */
    public User findByLoginName(String loginName) {
        return userDao.findByLoginName(loginName);
    }

    /**
     * 根据repoUser信息查询RepoUser实体
     *
     * @param repoUser
     * @return
     */
    public boolean findRepoUser(RepoUser repoUser) {
        RepoUser curRepoUser = repoUserDao.findByOwnerIdAndRepoAndUserNameAndPassword(
                repoUser.getOwnerId(),repoUser.getRepo(),repoUser.getUserName(),repoUser.getPassword());
        return curRepoUser!=null;
    }

    /**
     * 注册
     *
     * @param user
     */
    public void register(User user) {
        user.setId(idWorker.nextId() + "");
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRegDate(new Date());
        user.setRepoUserReleaseId(addRepoUser(user.getId(),"release"));
        user.setRepoUserSnapshotId(addRepoUser(user.getId(),"snapshot"));
        userDao.save(user);
    }


    /**
     * 增加一条RepoUser记录
     *
     */
    public String addRepoUser(String ownerId, String repo){
        RepoUser repoUser = new RepoUser();
        repoUser.setId(idWorker.nextId() + "");
        repoUser.setOwnerId(ownerId);
        repoUser.setRepo(repo);
        repoUser.setRepoDir(url+ownerId+"/"+repo);
        repoUser.setUserName(Base64.getEncoder().encodeToString((idWorker.nextId() + "").getBytes()));
        repoUser.setPassword(Base64.getEncoder().encodeToString((idWorker.nextId() + "").getBytes()));
        repoUserDao.save(repoUser);
        return repoUser.getId();
    }


    /**
     * 删除一条RepoUser记录
     *
     */
    public void deleteRepoUser(String id){
        RepoUser repoUser = repoUserDao.findById(id).get();
        repoUserDao.delete(repoUser);
    }


    /**
     * 获得一条RepoUser记录
     *
     */
    public RepoUser getRepoInfo(String id){
        RepoUser repoUser = repoUserDao.findById(id).get();
        return repoUser;
    }


    /**
     * 用户修改个人信息
     *
     * @param user
     */
    public void update(User user, String userId) {
        User oriUser = mergeUserInfo(user, userId);
        oriUser.setUpdateDate(new Date());
        userDao.save(oriUser);
    }


    /**
     * 修改用户信息合并信息
     *
     * @param user
     * @param id
     */
    public User mergeUserInfo(User user, String id) {
        User oriUser = userDao.findById(id).get();
        if (user.getPassword() != null && !"".equals(user.getPassword())) {
            oriUser.setPassword(encoder.encode(user.getPassword()));
        }

        if (user.getEmail() != null && !"".equals(user.getEmail()) && !user.getEmail().equals(oriUser.getEmail())) {
            User existUser = userDao.findByEmail(user.getEmail());
            if (existUser != null) {
                throw new RuntimeException("该邮箱已被注册");
            }
            oriUser.setEmail(user.getEmail());
        }
        if (user.getLoginName() != null && !"".equals(user.getLoginName()) && !user.getLoginName().equals(oriUser.getLoginName())) {
            User existUser = userDao.findByLoginName(user.getLoginName());
            if (existUser != null) {
                throw new RuntimeException("该用户名已被注册");
            }
            oriUser.setLoginName(user.getLoginName());
        }
        return oriUser;
    }


    /**
     * 登录
     *
     * @param user
     */
    public User login(User user) {
        //先根据用户名查询对象
        List<User> userList = userDao.findByLoginNameOrEmail(user.getLoginName(), user.getEmail());
        //将数据库中的密码与用户输入的密码进行比较
        if (userList != null && userList.size() == 1 && encoder.matches(user.getPassword(), userList.get(0).getPassword())) {
            //登录成功
            userList.get(0).setLastDate(new Date());
            userDao.save(userList.get(0));
            return userList.get(0);
        }
        //登录失败
        return null;
    }
}