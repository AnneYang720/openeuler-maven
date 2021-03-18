package com.openeuler.user.service;

import com.openeuler.user.dao.RepoUserDao;
import com.openeuler.user.dao.UserDao;
import com.openeuler.user.pojo.RepoUser;
import com.openeuler.user.pojo.User;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import util.IdWorker;
import util.JwtUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.*;

/**
 * 服务层
 *
 * @author Administrator
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
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    /**
     * 鉴定管理员身份
     *
     * @return
     */
    public void adminAuthentication() {
        String token = (String) request.getAttribute("claims_admin");
        //System.out.println("token = " + token);
        if (token == null || "".equals(token)) {
            throw new RuntimeException("权限不足");
        }
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<User> findAll() {
        adminAuthentication();
        return userDao.findAll();
    }

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
//        String token = (String) request.getAttribute("claims_user");
//        System.out.println("token = " + token);
//        if (token == null || "".equals(token)) {
//            throw new RuntimeException("登录超时");
//        }
//        String id = (String) request.getAttribute("user_id");
        User currUser = userDao.findById(userId).get();
        currUser.setId("");
        currUser.setPassword("");
        return currUser;
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<User> findSearch(Map whereMap) {
        adminAuthentication();
        Specification<User> specification = createSpecification(whereMap);
        return userDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    /**
     * 根据LoginName查询实体
     *
     * @param loginName
     * @return
     */
    public User findByLoginName(String loginName) {
        return userDao.findByLoginName(loginName);
    }

    /**
     * 管理员增加用户
     *
     * @param user
     */
    public void add(User user) {
        adminAuthentication();
        user.setId(idWorker.nextId() + "");
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRegDate(new Date());
        userDao.save(user);
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
        addRepoUser(user.getId(),"release");
        addRepoUser(user.getId(),"snapshot");
        userDao.save(user);
    }

    public String addRepoUser(String ownerId, String repo){
        RepoUser repoUser = new RepoUser();
        repoUser.setId(idWorker.nextId() + "");
        repoUser.setOwnerId(ownerId);
        repoUser.setRepo(repo);
        repoUser.setUser_name(Base64.getEncoder().encodeToString((idWorker.nextId() + "").getBytes()));
        repoUser.setPassword(Base64.getEncoder().encodeToString((idWorker.nextId() + "").getBytes()));
        repoUserDao.save(repoUser);
        return repoUser.getId();
    }

    public void deleteRepoUser(String id){
        RepoUser repoUser = repoUserDao.findById(id).get();
        repoUserDao.delete(repoUser);
    }
    /**
     * 修改
     *
     * @param user
     */
    public void update(User user, String userId) {
        User oriUser = mergeUserInfo(user, userId);
        oriUser.setUpdateDate(new Date());
        userDao.save(oriUser);
    }


    /**
     * 管理员修改用户信息
     *
     * @param user
     */
    public void updateById(User user, String id) {
        adminAuthentication();
        User oriUser = mergeUserInfo(user, id);
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
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        adminAuthentication();
        userDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<User> createSpecification(Map searchMap) {

        return new Specification<User>() {

            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
                }
                // 登录名
                if (searchMap.get("loginname") != null && !"".equals(searchMap.get("loginname"))) {
                    predicateList.add(cb.like(root.get("loginname").as(String.class), "%" + (String) searchMap.get("loginname") + "%"));
                }
                // 密码
                if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                    predicateList.add(cb.like(root.get("password").as(String.class), "%" + (String) searchMap.get("password") + "%"));
                }
                // 昵称
                if (searchMap.get("nickname") != null && !"".equals(searchMap.get("nickname"))) {
                    predicateList.add(cb.like(root.get("nickname").as(String.class), "%" + (String) searchMap.get("nickname") + "%"));
                }
                // E-Mail
                if (searchMap.get("email") != null && !"".equals(searchMap.get("email"))) {
                    predicateList.add(cb.like(root.get("email").as(String.class), "%" + (String) searchMap.get("email") + "%"));
                }

                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

            }
        };

    }

    /**
     * 登陆
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