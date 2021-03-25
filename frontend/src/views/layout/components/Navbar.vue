<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened"></hamburger>
    <breadcrumb></breadcrumb>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="avatar-wrapper">
        <img class="user-avatar" :src="avatar+'?imageView2/1/w/80/h/80'">
        <i class="el-icon-caret-bottom"></i>
      </div>
      <el-dropdown-menu class="user-dropdown" slot="dropdown">
        <el-dropdown-item>
          <span @click="dialogVisible = true" type="text" size="small">个人设置</span>
        </el-dropdown-item>
        <el-dropdown-item divided>
          <span @click="logout" style="display:block;">退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

    <el-dialog
      title="个人设置" 
      :visible.sync="dialogVisible"
      width="30%"
      >
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="pojo.loginName" :placeholder="loginName"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="pojo.email" :placeholder="email"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="pojo.password"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="infoSave()">保 存</el-button>
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </el-menu>
  
</template>


<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import {updateInfo} from '@/api/login'

export default {
  data(){
        return {
            list:[],
            dialogVisible: false, //编辑窗口是否可见
            pojo:{},//编辑实体
        }
    },
  components: {
    Breadcrumb,
    Hamburger
  },
  computed: {
    ...mapGetters([
      'loginName',
      'email',
      'sidebar',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // 为了重新实例化vue-router对象 避免bug
      })
    },
    infoSave(){
      updateInfo(this.pojo).then(response =>{
        this.$message({
          message: response.message,
          type: (response.flag ? 'success':'error')
        });
        if(response.flag){//如果成功
          this.$store.dispatch('GetInfo').then(() => {
            location.reload()//刷新页面
          })
        }
      })      
      
      this.dialogVisible = false // 关闭窗口
    }

  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .screenfull {
    position: absolute;
    right: 90px;
    top: 16px;
    color: red;
  }
  .avatar-container {
    height: 50px;
    display: inline-block;
    position: absolute;
    right: 35px;
    .avatar-wrapper {
      cursor: pointer;
      margin-top: 5px;
      position: relative;
      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 10px;
      }
      .el-icon-caret-bottom {
        position: absolute;
        right: -20px;
        top: 25px;
        font-size: 12px;
      }
    }
  }
}
</style>

