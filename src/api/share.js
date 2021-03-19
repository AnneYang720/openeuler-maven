import request from '@/utils/request'
export default{
    shareGetList(){
        return request({
            url: `/share/getlist`,//ES6写法
            method: 'get'
        });
    },
    shareSearch(keywords){
        return request({
            url: `/share/search?q=${keywords}`,//ES6写法
            method: 'get'
        });
    },
    getShareUsers(page,size){
        return request({
            url: `/share/userlist/${page}/${size}`,//ES6写法
            method: 'get'
        });
    },
    getSharedUsers(page,size){
        return request({
            url: `/share/shareduserlist/${page}/${size}`,//ES6写法
            method: 'get'
        });
    },
    addShareUser(pojo){
        return request({
            url: '/share/adduser',
            method: 'post',
            data: pojo
        })
    },
    deleteShare(userId){
        return request({
            url:`/share/delete/${userId}`,
            method:'delete'
        })
    },
    quitShare(userId){
        return request({
            url:`/share/quit/${userId}`,
            method:'delete'
        })
    },
    getRepoUserInfo(userId){
        return request({
            url:`/share/getrepouserinfo/${userId}`,
            method:'get'
        })
    }
} 