<template>
  <div>
    <br>
    <el-form :inline="true" >
      <el-form-item label="包名称">
        <el-input v-model="searchMap.name"></el-input>
      </el-form-item>
      <el-button @click="fetchData()" type="primary" plain>搜索</el-button>
      <el-button @click="dialogVisible = true;pojo={};id=''" type="primary" plain>新增</el-button>
    </el-form>

    <el-table
      :data="list"
      :row-style="{height:0+'px'}"
      :header-cell-style="{'text-align':'center'}"
      :cell-style="{padding:0+'px','text-align':'center'}"
      align="center"
      border
      style="width: 90%">
      <el-table-column
        prop="groupid"
        label="包名"
        width="180">
      </el-table-column>
      <el-table-column
        prop="latestversion"
        label="最新版本"
        width="180">
      </el-table-column>
      <el-table-column
        prop="updatetime"
        label="最近更新时间">
      </el-table-column>
      <el-table-column
        prop="versionnum"
        label="版本数">
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="100">
        <template slot-scope="scope">
          <el-button @click="handleDel(scope.row.id)" type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="fetchData"
      @current-change="fetchData"
      :current-page="currentPage"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- 弹出窗口 -->
    <el-dialog
      title="上传文件" 
      :visible.sync="dialogVisible"
      width="40%"
      >
      <el-form label-width="80px">
        <el-form-item label="GroupID">
          <el-input v-model="pojo.groupid" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="ArtifactID">
          <el-input v-model="pojo.artifactid" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="Version">
          <el-input v-model="pojo.version" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="Packaging">
          <el-input v-model="pojo.packaging" placeholder="请输入"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
        <el-button type="primary" @click="handleSave()">上 传</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>

import mavenApi from '@/api/maven'

export default {
    data(){
        return {
          list:[],
          total: 0,
          currentPage: 1,
          pageSize: 10,
          searchMap: {},
          dialogVisible: false,
          pojo:{}//编辑实体
        }
    },
    created () {
        this.fetchData()
    },
    methods: {
        fetchData(){
            mavenApi.search(this.$router.currentRoute.name,this.currentPage,this.pageSize,this.searchMap).then(response =>{
                this.total = response.data.total
                this.list = response.data.rows
            }).catch(() => {
                this.total = 0
                this.list = []
          });
        },
        // fetchData(){
        //     mavenApi.getList(this.$router.currentRoute.name).then(response =>{
        //         this.list = response.data
        //     })
        // },

        handleSave(){
          mavenApi.save(this.$router.currentRoute.name, this.pojo).then(response =>{
            this.$message({
              message: response.message,
              type: (response.flag ? 'success':'error')
            });
            if(response.flag){//如果成功
              this.fetchData()//刷新页面
            }
          })
            
          this.dialogVisible = false // 关闭窗口
        },

        handleDel(id){
            this.$confirm('您确定要删除此记录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            mavenApi.deleteById(this.$router.currentRoute.name,id).then(response =>{
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
          
        }
    },
    watch: {
      '$route': 'fetchData'
    }
}
</script>

