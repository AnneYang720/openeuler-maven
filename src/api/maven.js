import request from '@/utils/request'
export default{
    getList(repo,page,size){
        return request({
            url: `/maven/${repo}/getlist/${page}/${size}`,//ES6写法
            method: 'get'
        });
    },

    search(repo,page,size,keywords){
        return request({
            url: `/maven/${repo}/search/${page}/${size}?q=${keywords}`,//ES6写法
            method: 'get',
        });
    },

    createURL(repo,pojo){
        return request({
            url: `/maven/${repo}`,
            method: 'post',
            data: pojo
        })
    },

    save(repo,pojo){
        return request({
            url: `/maven/save/${repo}`,
            method: 'post',
            data: pojo        })
    },

    deleteGroup(repo,groupId,artifactId){
        //console.log("js delete this group")
        return request({
            url:`/maven/${repo}/${groupId}/${artifactId}/`,
            method:'delete'
        })
    },

    deleteVersion(repo,groupId,artifactId,chosenVersion){
        //console.log("js delete this version")
        return request({
            url:`/maven/${repo}/${groupId}/${artifactId}/${chosenVersion}/`,
            method:'delete'
        })
    },

    getUrl(repo,groupId,artifactId,chosenVersion){
        return request({
            url: `/maven/${repo}/geturl/${groupId}/${artifactId}/${chosenVersion}/`,
            method: 'get'
        })
    },

    getShareUrl(groupId,artifactId,chosenVersion,userId){
        return request({
            url: `/maven/getshareurl/${groupId}/${artifactId}/${chosenVersion}/${userId}/`,
            method: 'get'
        })
    }
} 
