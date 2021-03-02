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
            url: `/share/search`,//ES6写法
            method: 'post',
            data: keywords
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
    }
} 
