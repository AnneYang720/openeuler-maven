<template>
  <div>
    <br>
    <el-form :inline="true" align="right">
      <el-form-item label="包名称">
        <el-input v-model="keywords"></el-input>
      </el-form-item>
      <el-button @click="handleSearch()" type="primary" plain>搜索</el-button>
      <el-button @click="dialogVisible = true" type="primary" style="margin-right:5%" plain>新增</el-button>
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
        fixed="right"
        label="操作"
        width="100">
        <template slot-scope="scope">
          <el-button @click.native.stop="handleDel(scope.row)" type="text" size="small">删除</el-button>
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
      :total="total"
      align="center"
      style="margin-top:3%">
    </el-pagination>

    <!-- 弹出窗口 -->
    <el-dialog
      title="上传文件" 
      :visible.sync="dialogVisible"
      width="40%"
      >
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadForm" label-width="100px" style="width:95%">
        <el-form-item label="GroupID" prop="groupId">
          <el-input v-model="uploadForm.groupId" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="ArtifactID" prop="artifactId">
          <el-input v-model="uploadForm.artifactId" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="Version" prop="version">
          <el-input v-model="uploadForm.version" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="Packaging" prop="packaging">
          <el-input v-model="uploadForm.packaging" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="JAR File">
          <el-upload
            ref="uploadJAR"
            :action="uploadJARUrl"
            :multiple="false"
            accept=".jar"
            :auto-upload="false"
            :show-file-list="true"
            :file-list="JARfileList"
            :on-change="handleJARUploadChange">
            <el-button type="primary" slot="trigger">选取文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="POM File">
          <el-upload
            ref="uploadPOM"
            :action="uploadPOMUrl"
            :multiple="false"
            accept=".xml"
            :auto-upload="false"
            :show-file-list="true"
            :file-list="POMfileList"
            :on-change="handlePOMUploadChange">
            <el-button type="primary" slot="trigger">选取文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer" style="margin-right:10%">
        <el-button @click="dialogVisible = false">关 闭</el-button>
        <el-button type="primary" @click="handleUpload()">上 传</el-button>
      </span>
    </el-dialog>

    <!-- 弹出窗口 -->
    <el-dialog
      :title="packageName"
      :visible.sync="detailVisible"
      width="45%"
      >

      <div>
      <div style="line-height:40px;margin-left:5%"> <span>上传用户</span><span style="color:black;margin-left:27px">{{loginName}}</span> </div>
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


      <el-button @click="handleDelVersion()" type="primary" style="margin-top:20px;margin-left:75%" size="small">删除此版本</el-button>

    </div>
    </el-dialog>

  </div>
</template>
<script>

import mavenApi from '@/api/maven'
import { mapGetters } from 'vuex'
import axios from 'axios'
import {pFileReader} from '@/utils/filereader'

export default {
    data(){
        return {
          uploadDate: '', //当前文件的上传时间
          list:[], //首页用包名得到的列表
          urllist:[], //jar,pom包的下载地址
          total: 0, //总条数
          curRow: {}, //当前选中行的所有信息
          currentPage: 1, //当前页数
          pageSize: 10, //每页条数
          keywords: '', //搜索关键词
          dialogVisible: false, //新建包的弹出框
          detailVisible: false, //点开某行展示具体内容的弹出框
          saveFlag:true, //两个文件通过url直接上传是否成功
          JARfileList: [], //用户上传的jar文件
          POMfileList: [], //用户上传的pom文件
          uploadJARUrl: '', //后端返回的jar上传url
          uploadPOMUrl: '', //后端返回的pom上传url
          packageName: '', //当前点开行的包名
          chosenVersion: '', //当前选中的版本号
          vList: [], //记录某一行的versionList
          uploadForm: {
            groupId: '',
            artifactId: '',
            version: '',
            packaging: '',
          },
          uploadRules: {
            groupId: [{ required: true, message: '请输入groupId', trigger: 'blur'}],
            artifactId: [{ required: true, message: '请输入artifactId', trigger: 'blur'}],
            version: [{ required: true, message: '请输入version', trigger: 'blur'}],
            packaging: [{ required: true, message: '请输入packaging', trigger: 'blur'}]
          },
          
        }
    },
    created () {
        this.fetchData()
    },
    methods: {
        fetchData(){
            mavenApi.getList(this.$router.currentRoute.name,this.currentPage,this.pageSize).then(response =>{
                //this.total = response.data.total
                this.list = response.data
                //this.urllist = response.data.urls
            }).catch(() => {
                this.total = 0
                this.list = []
          });
        },

        handleSearch(){
          if (this.keywords!=='') {
            mavenApi.search(this.$router.currentRoute.name, this.currentPage, this.pageSize, this.keywords).then(response =>{
                this.list = response.data
            }).catch(() => {
                this.total = 0
                this.list = []
            });
          }
        },
        
        handleUpload(){
          if(this.beforeUploadJAR() && this.beforeUploadPOM()){
            this.$refs.uploadForm.validate(valid => {
              if (valid) {
                mavenApi.createURL(this.$router.currentRoute.name, this.uploadForm).then(async(response) => {
                  if(response.flag){
                    this.uploadJARUrl = response.data.uploadJARUrl
                    this.uploadPOMUrl = response.data.uploadPOMUrl

                    let e = await pFileReader(this.JARfileList[0].raw)
                    let res = await axios.put(this.uploadJARUrl, new Buffer(e.target.result, 'binary'))
                    console.log(res)
                    if(res.status!=200){
                      this.saveFlag = false
                      this.$message.error('JAR文件上传失败')
                      throw new Error('JAR文件上传失败！')
                    }

                    e = await pFileReader(this.POMfileList[0].raw)
                    res = await axios.put(this.uploadPOMUrl, new Buffer(e.target.result, 'binary'))
                    console.log(res)
                    if(res.status!=200){
                      this.saveFlag = false
                      this.$message.error('POM文件上传失败')
                      throw new Error('POM文件上传失败！')
                    }
                    
                    if(this.saveFlag){
                      console.log('save')
                      mavenApi.save(this.$router.currentRoute.name, this.uploadForm).then(response =>{
                        this.$message({
                          message: response.message,
                          type: (response.flag ? 'success':'error')
                        });
                        if(response.flag){
                            this.fetchData()
                          }
                        })
                    }else{
                      this.$message.error('文件上传失败')
                      return false
                    }
                    
                  }else {
                    console.log('创建上传URL失败!!')
                    return false
                  }
                })
              }else {
                console.log('error submit!!')
                this.$message.error('信息格式错误')
                return false
              }
            })
          }
          this.dialogVisible = false // 关闭窗口
        },

        handleDel(row){
            this.$confirm('您确定要删除此maven包吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            //console.log("delete this group")
            mavenApi.deleteByGroup(this.$router.currentRoute.name,row.groupId,row.artifactId).then(response =>{
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

        handleDelVersion(){
          this.$confirm('您确定要删除此版本吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            //console.log("delete this version")
            mavenApi.deleteVersion(this.$router.currentRoute.name,this.curRow.groupId,this.curRow.artifactId,this.chosenVersion).then(response =>{
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

        beforeUploadJAR(){
          if(this.JARfileList.length==0){
            this.$message.error('jar文件必须上传')
            return false
          }
          const fileType = this.JARfileList[0].name.substring(this.JARfileList[0].name.lastIndexOf('.'))
          if (fileType.toLowerCase() != '.jar') {
            this.$message.error('文件必须为jar类型')
            return false
          }
          return true
        },

        beforeUploadPOM(){
          if(this.POMfileList.length==0){
            this.$message.error('pom文件必须上传')
            return false
          }
          const fileType = this.POMfileList[0].name.substring(this.POMfileList[0].name.lastIndexOf('.'))
          if (fileType.toLowerCase() != '.xml') {
            this.$message.error('文件必须为pom类型')
            return false
          }
          return true
        },

        // 限制文件上传的个数只有一个，获取上传列表的最后一个
        handleJARUploadChange(file, JARfileList) {
            if (JARfileList.length > 0) {
                this.JARfileList = [JARfileList[JARfileList.length - 1]] // 这一步，是 展示最后一次选择的文件
            }
        },

        // 限制文件上传的个数只有一个，获取上传列表的最后一个
        handlePOMUploadChange(file, POMfileList) {
            if (POMfileList.length > 0) {
                this.POMfileList = [POMfileList[POMfileList.length - 1]] // 这一步，是 展示最后一次选择的文件
            }
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
          let dt = new Date(row.updateTime)
          this.uploadDate = dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds()
          mavenApi.getUrl(this.$router.currentRoute.name,row.groupId,row.artifactId,this.chosenVersion).then(response =>{
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
          mavenApi.getUrl(this.$router.currentRoute.name,this.curRow.groupId,this.curRow.artifactId,this.chosenVersion).then(response =>{
            this.urllist = response.data
            let dt = new Date(this.urllist[0].updateTime)
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
    },
    computed: {
    ...mapGetters([
      'loginName'
    ])
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