package com.openeuler.share.client;

import com.openeuler.share.pojo.SharedFileInfo;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value="openeuler-storage")
public interface StorageClient {
    @RequestMapping( value ="/maven/getlist/byid", method = RequestMethod.POST)
    List<SharedFileInfo> getListById(@RequestBody String id);
}