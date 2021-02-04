import request from '@/utils/request'
export default{
    getList(repo){
        return request({
            url:`/maven/${repo}`,
            method:'get'
        });
    },

    search(repo,page,size){
        return request({
            url: `/maven/${repo}/search/${page}/${size}`,//ES6写法
            method: 'get'
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

    deleteByGroup(repo,groupId,artifactId){
        //console.log("js delete this group")
        return request({
            url:`/maven/${repo}/${groupId}/${artifactId}`,
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
    }
} 
