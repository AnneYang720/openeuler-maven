<template>
  <div>
    <br>
    <el-form :inline="true" align="right">
      <el-form-item label="包名称">
        <el-input v-model="keywords"></el-input>
      </el-form-item>
      <el-button @click="shareSearch()" type="primary" style="margin-right:5%" plain>搜索</el-button>
    </el-form>

    <el-table
      :data="list"
      :row-style="{height:0+'px'}"
      :header-cell-style="{'text-align':'center'}"
      :cell-style="{padding:0+'px','text-align':'center'}"
      border
      @row-click="rowClick"
      style="width:90%;margin-left:5%">
      <el-table-column
        prop="groupId"
        label="GroupId"
        width="160">
        <template slot-scope="scope">
          <span style="color: DodgerBlue">{{ scope.row.groupId }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="artifactId"
        label="ArtifactId"
        width="160">
        <template slot-scope="scope">
          <span style="color: DodgerBlue">{{ scope.row.artifactId }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="latestVersion"
        label="最新版本"
        width="160">
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="最近更新时间"
        :formatter="formatDate">
      </el-table-column>
      <el-table-column
        prop="versionNum"
        label="版本数">
      </el-table-column>
      <el-table-column
        prop="uploadUser"
        label="上传用户">
      </el-table-column>
    </el-table>
    
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      align="center"
      style="margin-top:3%">
    </el-pagination>


    <!-- 弹出窗口 -->
    <el-dialog
      :title="packageName"
      :visible.sync="detailVisible"
      width="45%"
      >

      <div>
      <div style="line-height:40px;margin-left:5%"> <span>上传用户</span><span style="color:black;margin-left:27px">{{currUser}}</span> </div>
      <div style="line-height:40px;margin-left:5%"> <span>上传时间</span><span style="color:black;margin-left:27px">{{uploadDate}}</span> </div>

      <div style="line-height:40px;margin-left:5%"> 版本
      <el-select v-model="chosenVersion" @change="urlChange" style="margin-left:50px">
        <el-option v-for="item in vList" :label="item" :key="item" :value="item"/>
      </el-select>
      </div>

      <el-table
        :data="urllist"
        :row-style="{height:0+'px'}"
        :header-cell-style="{'text-align':'center'}"
        :cell-style="{padding:5+'px','text-align':'center'}"
        border
        style="width: 90%;margin-top:30px;margin-left:5%">
        <el-table-column
          prop="filename"
          label="文件名">
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作">

          <template slot-scope="scope">
          <a @click="downFile(scope.row.downloadUrl)"  type="text/html">下载</a>
          </template>
    
          <!-- <template slot-scope="scope">
            <el-button @click="handleDownload(scope.row.downloadurl)" type="text" size="small">下载</el-button>
          </template> -->
        </el-table-column>
      </el-table>

    </div>
    </el-dialog>


  </div>
</template>
<script>

import mavenApi from '@/api/maven'
import shareApi from '@/api/share'

export default {
    data(){
        return {
          currUser: '', //当前用户昵称
          uploadDate: '', //当前文件的上传时间
          list:[], //首页用包名得到的列表
          urllist:[], //jar,pom包的下载地址
          total: 0, //总页数
          curRow: {}, //当前选中行的所有信息
          currentPage: 1, //当前页数
          pageSize: 5, //每页条数
          keywords: '', //搜索关键词
          detailVisible: false, //点开某行展示具体内容的弹出框
          packageName: '', //当前点开行的包名
          chosenVersion: '', //当前选中的版本号
          vList: [], //记录某一行的versionList
          
        }
    },
    created () {
        this.fetchData()
    },
    methods: {
        fetchData(){
            shareApi.shareGetList().then(response =>{
                console.log(this.currentPage)
                console.log(this.pageSize)
                let start = (this.currentPage-1)*this.pageSize
                let end = this.currentPage*this.pageSize
                this.total = response.data.length
                this.list = response.data.slice(start, end)
            }).catch(() => {
                this.total = 0
                this.list = []
          });
        },

        handleSizeChange(val) {
          this.pageSize = val;
          if(this.keywords==='') this.fetchData();
          else this.shareSearch();
        },

        handleCurrentChange(val) {
          this.currentPage = val;
          if(this.keywords==='') this.fetchData();
          else this.shareSearch();
        },

        shareSearch(){
          if(this.keywords==='') this.fetchData();
          else {
            shareApi.shareSearch(this.keywords).then(response =>{
                let start = (this.currentPage-1)*this.pageSize
                let end = this.currentPage*this.pageSize
                this.total = response.data.length
                this.list = response.data.slice(start, end)
            }).catch(() => {
                this.total = 0
                this.list = []
            });
          }
        },
        
        downFile (downloadurl) {
          //console.log(downloadurl)
          var ele = document.createElement('a')
          ele.download = downloadurl
          ele.style.display = 'none';
          ele.href = downloadurl
          ele.target="_blank"; // 针对 ie模式 的浏览器
          // 触发点击
          document.body.appendChild(ele);
          ele.click();
          // 然后移除
          document.body.removeChild(ele);
        },


        formatDate(row, column) {
          let data = row[column.property]
          let dt = new Date(data)
          return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
        },

        //监听row-click事件，实现选中
        rowClick(row, column, event) {
          this.detailVisible = true
          this.packageName = row.groupId+":"+row.artifactId
          this.vList = row.versionList
          this.vList.reverse()
          this.curRow = row
          this.chosenVersion = row.latestVersion//this.vList[0].value
          this.currUser = row.uploadUser
          let dt = new Date(row.updateTime)
          this.uploadDate = dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
          mavenApi.getShareUrl(row.groupId,row.artifactId,this.chosenVersion,row.userId).then(response =>{
            this.urllist = response.data
          }).catch(() => {
            this.$message({
              message: response.message,
              type: (response.flag ? 'success':'error')
            })
          })
        },

        //更改版本后获得新的url
        urlChange(){
          //console.log(this.curRow)
          mavenApi.getShareUrl(this.curRow.groupId,this.curRow.artifactId,this.chosenVersion,this.curRow.userId).then(response =>{
            this.urllist = response.data
            let dt = new Date(this.urllist[0].updateDate)
            this.uploadDate = dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
          }).catch(() => {
            this.$message({
              message: response.message,
              type: (response.flag ? 'success':'error')
            })
          })
        },
    },
    watch: {
      '$route': 'fetchData'
    }
}
      
</script>

<style rel="stylesheet/scss" lang="scss">
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