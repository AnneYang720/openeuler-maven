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
    public void addShareUser(User user, String myId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(myId, user.getId());
        if(curInfo.size()!=0){ throw new RuntimeException("用户已经存在"); }
        ShareInfo shareinfo = new ShareInfo();
        shareinfo.setId(idWorker.nextId() + "");
        shareinfo.setUserId(myId);
        shareinfo.setSharedUserId(user.getId());
        shareDao.save(shareinfo);
    }

    public void deleteShare(String myId,String userId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(myId, userId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
    }

    public void quitShare(String myId,String userId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(userId, myId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
    }

    public List<ShareInfo> getShareUsers(int page, int size,String userId) {
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findByUserId(userId, pageable);
    }

    public List<ShareInfo> getSharedUsers(String userId) {
        return shareDao.findBySharedUserId(userId);
    }

    public List<ShareInfo> getSharedUsers(int page, int size,String userId) {
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findBySharedUserId(userId, pageable);
    }
}
