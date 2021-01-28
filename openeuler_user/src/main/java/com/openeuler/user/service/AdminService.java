package com.openeuler.user.service;

import com.openeuler.user.dao.AdminDao;
import com.openeuler.user.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务层
 *
 * @author Administrator
 *
 */
@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder encoder;

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
    public List<Admin> findAll() {
        adminAuthentication();
        return adminDao.findAll();
    }


//    /**
//     * 条件查询+分页
//     * @param whereMap
//     * @param page
//     * @param size
//     * @return
//     */
//    public Page<Admin> findSearch(Map whereMap, int page, int size) {
//        Specification<Admin> specification = createSpecification(whereMap);
//        PageRequest pageRequest =  PageRequest.of(page-1, size);
//        return adminDao.findAll(specification, pageRequest);
//    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Admin> findSearch(Map whereMap) {
        adminAuthentication();
        Specification<Admin> specification = createSpecification(whereMap);
        return adminDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    public Admin findById(String id) {
        adminAuthentication();
        return adminDao.findById(id).get();
    }

    /**
     * 增加
     * @param admin
     */
    public void add(Admin admin) {
        adminAuthentication();
        admin.setId(idWorker.nextId()+"" );
        //密码加密
        admin.setPassword(encoder.encode(admin.getPassword()));
        adminDao.save(admin);
    }

    /**
     * 修改
     *
     * @param admin
     */
    public void update(Admin admin) {
        adminAuthentication();
        String id = (String) request.getAttribute("admin_id");
        Admin oriAdmin = mergeAdminInfo(admin, id);
        adminDao.save(oriAdmin);
    }

    /**
     * 修改其他管理员信息
     *
     * @param admin
     */
    public void updateById(Admin admin, String id) {
        adminAuthentication();
        Admin oriAdmin = mergeAdminInfo(admin, id);
        adminDao.save(oriAdmin);
    }

    /**
     * 修改用户信息合并信息
     *
     * @param admin
     * @param id
     */
    public Admin mergeAdminInfo(Admin admin, String id) {
        Admin oriAdmin = adminDao.findById(id).get();
        if (admin.getPassword() != null && !"".equals(admin.getPassword())) {
            oriAdmin.setPassword(encoder.encode(admin.getPassword()));
        }
        if (admin.getLoginName() != null && !"".equals(admin.getLoginName())) {
            Admin existUser = adminDao.findByLoginName(admin.getLoginName());
            if (existUser != null) {
                throw new RuntimeException("该用户名已被注册");
            }
            oriAdmin.setLoginName(admin.getLoginName());
        }
        return oriAdmin;
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        adminAuthentication();
        adminDao.deleteById(id);
    }

    /**
     * 动态条件构建
     * @param searchMap
     * @return
     */
    private Specification<Admin> createSpecification(Map searchMap) {

        return new Specification<Admin>() {

            @Override
            public Predicate toPredicate(Root<Admin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 登陆名称
                if (searchMap.get("loginname")!=null && !"".equals(searchMap.get("loginname"))) {
                    predicateList.add(cb.like(root.get("loginname").as(String.class), "%"+(String)searchMap.get("loginname")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                    predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 状态
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                    predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }

                return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

            }
        };

    }

    public Admin login(Admin admin) {
        //先根据用户名查询对象
        Admin adminByDB = adminDao.findByLoginName(admin.getLoginName());
        //将数据库中的密码与用户输入的密码进行比较
        if (adminByDB != null && encoder.matches(admin.getPassword(),adminByDB.getPassword())) {
            //登录成功
            return adminByDB;
        }
        //登录失败
        return null;
    }
}