package com.openeuler.share.service;

import com.openeuler.storage.dao.FileDao;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ShareService {
    @Autowired
    private FileDao fileDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private HttpServletRequest request;

    public List<FileDao.ShareArtifactVersionList> getShareList(int page, int size) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆");
        }

        return this.fileDao.findShareVersionsGroupByArtifactAndGroupId(claims.getId(), page * size, size);
    }
}
