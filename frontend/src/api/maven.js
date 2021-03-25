import request from '@/utils/request'
export default{
    getList(repo){
        return request({
            url:`/maven/${repo}`,
            method:'get'
        });
    },

    search(repo,page,size,searchMap){
        return request({
            url: `/maven/${repo}/search/${page}/${size}`,//ES6写法
            method: 'post',
            data: searchMap
        });
    },

    save(repo,pojo){
        return request({
            url: `/maven/${repo}`,
            method: 'post',
            data: pojo
        })
    },

    deleteById(repo,id){
        return request({
            url:`/maven/${repo}/${id}`,
            method:'delete'
        })
    }
} 
