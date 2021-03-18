<template>
<div class="dashboard-container">
<el-row :gutter="5" type="flex" >
  <el-col :span="20">

    <span style="color:blue;font-size:20px;font-weight:bold;margin-left:5%"> 我分享的 </span>
    <el-button @click="openDialog" type="primary" style="margin-left:5%" plain>增加用户</el-button>
    <el-table
      :data="shareuserlist"
      :row-style="{height:0+'px'}"
      :header-cell-style="{'text-align':'center'}"
      :cell-style="{padding:0+'px','text-align':'center'}"
      border
      style="width:90%;margin-left:5%;margin-top:3%">
      <el-table-column
        prop="loginName"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="userEmail"
        label="用户邮箱">
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="100">
        <template slot-scope="scope">
          <el-button @click.native.stop="deleteShareUser(scope.row)" type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange1"
      @current-change="handleCurrentChange1"
      :current-page="currentPage1"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize1"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total1"
      align="center"
      style="margin-top:3%;margin-bottom:5%">
    </el-pagination>

    </el-col>
    <el-col :span="20">
    <span style="color:blue;font-size:20px;font-weight:bold;margin-left:5%"> 分享给我的 </span>
    <el-table
      :data="shareduserlist"
      :row-style="{height:0+'px'}"
      :header-cell-style="{'text-align':'center'}"
      :cell-style="{padding:0+'px','text-align':'center'}"
      border
      style="width:90%;margin-left:5%;margin-top:6%">
      <el-table-column
        prop="loginName"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="userEmail"
        label="用户邮箱">
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="100">
        <template slot-scope="scope">
          <el-button @click.native.stop="quitShare(scope.row)" type="text" size="small">退出</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange2"
      @current-change="handleCurrentChange2"
      :current-page="currentPage2"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize2"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total2"
      align="center"
      style="margin-top:3%">
    </el-pagination>
    
    </el-col>
    <!-- 弹出窗口 -->
    <el-dialog
      title="增加用户" 
      :visible.sync="dialogVisible"
      width="40%"
      >
      <el-form :model="addUserForm" :rules="addUserRules" ref="addUserForm" label-width="100px" style="width:90%">
        <el-form-item label="用户名" prop="loginName">
          <el-input v-model="addUserForm.loginName" placeholder="请输入"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer" style="margin-right:10%">
        <el-button @click="closeDialog">关 闭</el-button>
        <el-button type="primary" @click="addShare()">增 加</el-button>
      </span>
    </el-dialog>  
  

</el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import shareApi from '@/api/share'

export default {
  name: 'dashboard',
  computed: {
    ...mapGetters([
      'loginName',
      'email',
      'userId'
    ])
  },
  data(){
        return {
          shareuserlist:[], //主动分享的用户信息
          shareduserlist:[], //被分享的用户信息
          total1: 0, //总条数
          currentPage1: 1, //当前页数
          pageSize1: 5, //每页条数
          total2: 0, //总条数
          currentPage2: 1, //当前页数
          pageSize2: 5, //每页条数
          dialogVisible: false, //增加被分享用户的弹出框
          addUserForm: {
            loginName: ''
          },
          addUserRules: {
            loginName: [{ required: true, message: '请输入待分享用户的昵称', trigger: 'blur'}]
          },
          
      }
    },
    created () {
        this.fetchData()
    },
    methods: {
        fetchData(){
            this.fetchShareUsers();
            this.fetchSharedUsers();
        },

        fetchShareUsers(){
            shareApi.getShareUsers(this.currentPage1,this.pageSize1).then(response =>{
                //console.log(this.currentPage1)
                //console.log(this.pageSize1)
                this.total1 = response.data.total
                this.shareuserlist = response.data.rows
            }).catch(() => {
                this.total1 = 0
                this.shareuserlist = []
          });
        },

        fetchSharedUsers(){
            shareApi.getSharedUsers(this.currentPage2,this.pageSize2).then(response =>{
                //console.log(this.currentPage2)
                //console.log(this.pageSize2)
                this.total2 = response.data.total
                this.shareduserlist = response.data.rows
            }).catch(() => {
                this.total2 = 0
                this.shareduserlist = []
          });
        },

        handleSizeChange1(val) {
          this.pageSize1 = val;
          this.fetchShareUsers();
        },

        handleCurrentChange1(val) {
          this.currentPage1 = val;
          this.fetchShareUsers();
        },

        handleSizeChange2(val) {
          this.pageSize2 = val;
          this.fetchSharedUsers();
        },

        handleCurrentChange2(val) {
          this.currentPage2 = val;
          this.fetchSharedUsers();
        },

        addShare(){
          shareApi.addShareUser(this.addUserForm).then(response =>{
                this.$message({
                  message: response.message,
                  type: (response.flag ? 'success':'error')
                  });
                  if(response.flag){//如果成功
                    this.fetchData()
                    this.closeDialog()
                    this.addUserForm.loginName = ''
                  }
              })
        },

        deleteShareUser(row){
          this.$confirm('您确定要停止对此用户分享吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            shareApi.deleteShare(row.userId).then(response =>{
              this.$message({
                message: response.message,
                type: (response.flag ? 'success':'error')
              });
              if(response.flag){
                this.fetchData()
              }
            })
          }).catch(() => {
          });
        },

        quitShare(row){
          this.$confirm('您确定要退出此用户对您的分享吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            shareApi.quitShare(row.userId).then(response =>{
              this.$message({
                message: response.message,
                type: (response.flag ? 'success':'error')
              });
              if(response.flag){
                this.fetchData()
              }
            })
          }).catch(() => {
          });
        },

        closeDialog () {
          this.dialogVisible = false;
        },

        openDialog () {
          this.dialogVisible = true;
          this.$nextTick(()=>{
            this.$refs['addUserForm'].resetFields()
          })
        }
    },
    watch: {
      '$route': 'fetchData'
    }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.dashboard {
  &-container {
    margin: 30px;
  }
}

.el-dialog {
  // // transform: translateY(-50%);
  // //border-radius: 10px;
  // // width: 500px;
  // // height: 500px!important;
  .el-dialog__header{  
    background: #f7f7f7;
    text-align: left;   
    font-weight: 600;
  }
}
</style>
