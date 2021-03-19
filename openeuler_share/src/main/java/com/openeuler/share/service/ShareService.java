package com.openeuler.share.service;

import com.openeuler.share.dao.ShareDao;
import com.openeuler.share.pojo.ShareInfo;
import com.openeuler.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.List;

@Service
public class ShareService {
    @Autowired
    private ShareDao shareDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 判断分享关系是否存在
     *
     * @param user
     * @param myId
     */
    public boolean ifExist(User user, String myId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(myId, user.getId());
        if(curInfo.size()!=0){
            return true;
        }
        return false;
    }


    /**
     * 新增分享用户
     *
     * @param user
     */
    public void addShareUser(User user, String myId, String repoUserId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(myId, user.getId());
        if(curInfo.size()!=0){ throw new RuntimeException("用户已经存在"); }

        ShareInfo shareinfo = new ShareInfo();
        shareinfo.setId(idWorker.nextId() + "");
        shareinfo.setUserId(myId);
        shareinfo.setSharedUserId(user.getId());
        shareinfo.setRepoUserId(repoUserId);
        shareDao.save(shareinfo);
    }


    /**
     * 删除对某用户的分享
     *
     * @param myId
     * @param userId
     */
    public String deleteShare(String myId,String userId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(myId, userId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
        return curInfo.get(0).getRepoUserId();
    }


    /**
     * 退出某用户对自己的分享
     *
     * @param userId
     * @param myId
     */
    public String quitShare(String userId,String myId) {
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(userId, myId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        shareDao.delete(curInfo.get(0));
        return curInfo.get(0).getRepoUserId();
    }


    /**
     * 列举该用户分享的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     */
    public Page<ShareInfo> getShareUsers(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findByUserId(userId, pageable);
    }


    /**
     * 列举分享给该用户的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     * @param userId
     */
    public Page<ShareInfo> getSharedUsers(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page-1, size);
        return shareDao.findBySharedUserId(userId, pageable);
    }


    /**
     * 列举分享给该用户的用户信息
     *
     * @param userId
     */
    public List<ShareInfo> getSharedUsers(String userId) {
        return shareDao.findBySharedUserId(userId);
    }


    /**
     * 获得该分享关系对应的RepoUserId
     *
     * @param userId
     * @param myId
     */
    public String getRepoUserId(String userId,String myId){
        List<ShareInfo> curInfo = shareDao.findByUserIdAndSharedUserId(userId, myId);
        if(curInfo.size()!=1){ throw new RuntimeException("无法定位用户"); }
        return curInfo.get(0).getRepoUserId();
    }
}
