package com.openeuler.share.controller;

import com.openeuler.share.client.StorageClient;
import com.openeuler.share.client.UserClient;
import com.openeuler.share.pojo.ShareInfo;
import com.openeuler.share.pojo.ShareUserInfo;
import com.openeuler.share.pojo.SharedFileInfo;
import com.openeuler.share.service.ShareService;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.user.pojo.User;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param page
     * @param size
     */
    @GetMapping("/getlist/{page}/{size}")
    public Result getShareListPage(@PathVariable int page, @PathVariable int size) {
        List<ShareInfo> usersId = shareService.getSharedUsers();
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
//
//    /**
//     * 根据关键词搜索
//     * 带分页，从第 page 页开始的 size 个
//     *
//     * @param page
//     * @param size
//     */
//    @PostMapping("/search/{page}/{size}")
//    public Result shareSearch(@PathVariable int page, @PathVariable int size) {
//        List<FileDao.ShareArtifactVersionList> data = shareService.getShareList(page, size);
//        return new Result(true, StatusCode.OK, "列举成功", data);
//    }

    /**
     * 列举分享的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     */
    @RequestMapping(value="/userlist/{page}/{size}", method = RequestMethod.GET)
    public Result getShareUsers(@PathVariable int page, @PathVariable int size) {
        List<ShareInfo> usersId = shareService.getShareUsers(page, size);
        List<ShareUserInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId){
            System.out.println("ShareUserId: "+info.getSharedUserId());
            User tmpUser = userClient.findUserById(info.getSharedUserId());
            data.add(new ShareUserInfo(tmpUser.getId(), tmpUser.getEmail(), tmpUser.getLoginName()));
        }
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 列举被分享的用户信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param page
     * @param size
     */
    @GetMapping("/shareduserlist/{page}/{size}")
    public Result getSharedUsers(@PathVariable int page, @PathVariable int size) {
        List<ShareInfo> usersId = shareService.getSharedUsers(page, size);
        List<ShareUserInfo> data = new ArrayList<>();
        for(ShareInfo info : usersId){
            System.out.println("UserId: "+info.getUserId());
            User tmpUser = userClient.findUserById(info.getUserId());
            data.add(new ShareUserInfo(tmpUser.getId(), tmpUser.getEmail(), tmpUser.getLoginName()));
        }
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 添加分享包用户
     *
     * @param user
     */
    @PostMapping("/adduser")
    public Result addShareUser(@RequestBody User user) {
        //System.out.println("loginName: "+user.getLoginName());
        User existUser = userClient.findByLoginName(user);
        //System.out.println(existUser.getId());
        if (existUser.getId() != null) {
            shareService.addShareUser(existUser);
            return new Result(true, StatusCode.OK, "添加分享成功");
        } else {
            return new Result(false, StatusCode.ERROR, "添加失败，用户名不存在");
        }
    }

    /**
     * 删除分享包用户
     *
     * @param userId
     */
    @RequestMapping(value ="/delete/{userId}", method = RequestMethod.DELETE)
    public Result deleteShare(@PathVariable String userId) {
        shareService.deleteShare(userId);
        return new Result(true, StatusCode.OK, "用户删除成功");
    }

    /**
     * 退出被某用户的分享
     *
     * @param userId
     */
    @RequestMapping(value ="/quit/{userId}", method = RequestMethod.DELETE)
    public Result quitShare(@PathVariable String userId) {
        shareService.quitShare(userId);
        return new Result(true, StatusCode.OK, "退出分享成功");
    }
}

