package com.openeuler.share.controller;

import com.openeuler.share.client.StorageClient;
import com.openeuler.share.client.UserClient;
import com.openeuler.share.pojo.ShareInfo;
import com.openeuler.share.pojo.ShareUserInfo;
import com.openeuler.share.pojo.SharedFileInfo;
import com.openeuler.share.service.ShareService;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.user.pojo.User;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/share")
public class ShareController {
    @Autowired
    private ShareService shareService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private StorageClient storageClient;

    /**
     * 列举被分享的包信息
     * 带分页，从第 page 页开始的 size 个
     *
     */
    @GetMapping("/getlist")
    public Result getShareListPage(@RequestHeader(value="X-User-Id") String userId) {
        List<ShareInfo> usersId = shareService.getSharedUsers(userId);
        List<SharedFileInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId){
            List<SharedFileInfo> tmpList = storageClient.getListById(info.getUserId());
            User tmpUser = userClient.findUserById(info.getUserId());
            for(SharedFileInfo fileinfo:tmpList){
                fileinfo.setUploadUser(tmpUser.getLoginName());
                fileinfo.setUserId(info.getUserId());
            }
            data.addAll(tmpList);
        }
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 根据关键词搜索
     * 带分页，从第 page 页开始的 size 个
     *
     * @param keywords
     */
    @GetMapping("/search")
    public Result shareSearch(@RequestParam(name = "q") String keywords, @RequestHeader(value="X-User-Id") String userId) {
        List<ShareInfo> usersId = shareService.getSharedUsers(userId);
        List<SharedFileInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId){
            List<SharedFileInfo> tmpList = storageClient.searchListById(info.getUserId(),keywords);
            User tmpUser = userClient.findUserById(info.getUserId());
            for(SharedFileInfo fileinfo:tmpList){
                fileinfo.setUploadUser(tmpUser.getLoginName());
                fileinfo.setUserId(info.getUserId());
            }
            data.addAll(tmpList);
        }
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 列举分享的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     */
    @RequestMapping(value="/userlist/{page}/{size}", method = RequestMethod.GET)
    public Result getShareUsers(@PathVariable int page, @PathVariable int size, @RequestHeader(value="X-User-Id") String userId) {
        Page<ShareInfo> usersId = shareService.getShareUsers(page, size, userId);
        List<ShareUserInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId.getContent()){
            //System.out.println("ShareUserId: "+info.getSharedUserId());
            User tmpUser = userClient.findUserById(info.getSharedUserId());
            data.add(new ShareUserInfo(tmpUser.getId(), tmpUser.getEmail(), tmpUser.getLoginName()));
        }
        return new Result(true, StatusCode.OK, "列举成功", new PageResult<ShareUserInfo>(usersId.getTotalElements(),data));
    }

    /**
     * 列举被分享的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     */
    @GetMapping("/shareduserlist/{page}/{size}")
    public Result getSharedUsers(@PathVariable int page, @PathVariable int size, @RequestHeader(value="X-User-Id") String userId) {
        Page<ShareInfo> usersId = shareService.getSharedUsers(page, size, userId);
        List<ShareUserInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId.getContent()){
            System.out.println("UserId: "+info.getUserId());
            User tmpUser = userClient.findUserById(info.getUserId());
            data.add(new ShareUserInfo(tmpUser.getId(), tmpUser.getEmail(), tmpUser.getLoginName()));
        }
        return new Result(true, StatusCode.OK, "列举成功", new PageResult<ShareUserInfo>(usersId.getTotalElements(),data));
    }

    /**
     * 添加分享包用户
     *
     * @param user
     */
    @PostMapping("/adduser")
    public Result addShareUser(@RequestBody User user, @RequestHeader(value="X-User-Id") String myId) {
        //System.out.println("loginName: "+user.getLoginName());
        try{
            User existUser = userClient.findByLoginName(user);
//            System.out.println(existUser.getId());
            if (existUser != null) {
                if(shareService.ifExist(existUser,myId)){
                    return new Result(false, StatusCode.ERROR, "添加失败，用户名已存在");
                }else{
                    String repoUserId = userClient.addShareUser(myId);
                    System.out.println("repoUserId: " + repoUserId);
                    shareService.addShareUser(existUser, myId, repoUserId);
                    return new Result(true, StatusCode.OK, "添加分享成功");
                }
            } else {
                return new Result(false, StatusCode.ERROR, "添加失败，用户名不存在");
            }
        }catch (Exception e){
            return new Result(false, StatusCode.ERROR, e.getMessage());
        }
    }

    /**
     * 删除分享包用户
     *
     * @param userId
     */
    @RequestMapping(value ="/delete/{userId}", method = RequestMethod.DELETE)
    public Result deleteShare(@PathVariable String userId, @RequestHeader(value="X-User-Id") String myId) {
        String repoUserId = shareService.deleteShare(myId, userId);
        userClient.deleteRepoUser(repoUserId);
        return new Result(true, StatusCode.OK, "用户删除成功");
    }

    /**
     * 退出被某用户的分享
     *
     * @param userId
     */
    @RequestMapping(value ="/quit/{userId}", method = RequestMethod.DELETE)
    public Result quitShare(@PathVariable String userId, @RequestHeader(value="X-User-Id") String myId) {
        String repoUserId = shareService.quitShare(userId, myId);
        userClient.deleteRepoUser(repoUserId);
        return new Result(true, StatusCode.OK, "退出分享成功");
    }

    /**
     * 返回他人分享给自己的仓库的repoUser的用户名和密码
     * @return
     */
    @RequestMapping(value = "/getrepouserinfo/{userId}", method = RequestMethod.GET)
    public Result getOthersRepoUserInfo(@PathVariable String userId, @RequestHeader(value="X-User-Id") String myId) {
        String repoUserId = shareService.getRepoUserId(userId,myId);
        System.out.println("repoUserId: "+repoUserId);
        return new Result(true, StatusCode.OK, "查询成功", userClient.getRepoInfo(repoUserId));
    }
}

