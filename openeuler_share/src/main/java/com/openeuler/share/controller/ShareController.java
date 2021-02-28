package com.openeuler.share.controller;

import com.openeuler.share.client.UserClient;
import com.openeuler.share.pojo.ShareInfo;
import com.openeuler.share.pojo.ShareUserInfo;
import com.openeuler.share.service.ShareService;
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
//
//    /**
//     * 列举被分享的包信息
//     * 带分页，从第 page 页开始的 size 个
//     *
//     * @param page
//     * @param size
//     */
//    @GetMapping("/getlist/{page}/{size}")
//    public Result getShareListPage(@PathVariable int page, @PathVariable int size) {
//        List<FileDao.ShareArtifactVersionList> data = shareService.getShareList(page, size);
//        return new Result(true, StatusCode.OK, "列举成功", data);
//    }
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
    @GetMapping("/userlist/{page}/{size}")
    public Result getShareUsers(@PathVariable int page, @PathVariable int size) {
//        List<ShareInfo> usersId = shareService.getShareUsers(page, size);
        List<ShareUserInfo> data = new ArrayList<>();
//        for(ShareInfo info : usersId){
//            User tmpUser = userClient.findUserById(info.getSharedUserId());
//            data.add(new ShareUserInfo(tmpUser.getId(), tmpUser.getEmail(), tmpUser.getLoginName()));
//        }
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
            User tmpUser = userClient.findUserById(info.getSharedUserId());
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
        User existUser = userClient.findByLoginName(user);
        if (existUser != null) {
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

