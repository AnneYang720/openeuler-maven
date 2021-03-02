package com.openeuler.share.service;

import com.openeuler.share.dao.ShareDao;
import com.openeuler.share.pojo.ShareInfo;
import com.openeuler.user.pojo.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ShareService {
    @Autowired
    private ShareDao shareDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private HttpServletRequest request;


    /**
     * 新增分享用户
     *
     * @param user
     */
    public void addShareUser(User user) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再删除用户");
        }
        System.out.println("claims.getId() "+claims.getId());
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(claims.getId(), user.getId());
        if(curInfo.size()!=0){ throw new RuntimeException("用户已经存在"); }
        ShareInfo shareinfo = new ShareInfo();
        shareinfo.setId(idWorker.nextId() + "");
        shareinfo.setUserId(claims.getId());
        shareinfo.setSharedUserId(user.getId());
        shareDao.save(shareinfo);
    }

    public void deleteShare(String userId) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再删除用户");
        }

        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(claims.getId(), userId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
    }

    public void quitShare(String userId) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再退出");
        }

        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(userId, claims.getId());
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
    }

    public List<ShareInfo> getShareUsers(int page, int size) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆");
        }
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findByUserId(claims.getId(), pageable);
    }

    public List<ShareInfo> getSharedUsers() {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆");
        }
        return shareDao.findBySharedUserId(claims.getId());
    }

    public List<ShareInfo> getSharedUsers(int page, int size) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆");
        }
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findBySharedUserId(claims.getId(), pageable);
    }
}
