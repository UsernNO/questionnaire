<template>
  <div class="main-content">
      <el-row :gutter="10">
        <el-col :span="8" v-for="item in pagesList" :key="item.id">
            <div class="card" style="margin-bottom: 10px">
                <div style="display:  flex; grid-gap: 5px; margin-bottom: 20px" >
                    <div style="flex: 1; width: 0">
                        <div style="margin-bottom: 10px; font-size: 20px" class="line1">{{item.name}}</div>
                        <div style="opacity: 0.6;" class="line1">{{item.descr}}</div>
                    </div>
                    <div style="width: 70px;height:50px">
                        <img :src = "item.img" alt="" style="width: 100%">
                    </div>
                </div>

                <div>
                    <el-button icon="el-icon-video-play" type="info" size="mini" @click="preview(item.id)">预览</el-button>
                    <el-button icon="" type="primary" size="mini" @click="copy(item.id)">使用模板</el-button>
                </div>

            </div>
        </el-col>

      </el-row>
  </div>
</template>

<script>
export default {

  data() {
    return {
        pagesList : [],
    }
  },
  mounted() {
        this.load();
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
      copy(pageId){
          this.$request.post('/pages/copy?pageId=' + pageId).then(res =>{
              if( res.code === '200'){
                  this.$message.success('复制成功');
                  setTimeout(() =>{
                      location.href = '/front/design?pageId=' + res.data;
                  },500)
              }else {
                  this.$message.error(res.msg);
              }
          })
      },
      preview(pageId){
          window.open('/front/preview?pageId=' + pageId)
      },
      load(){
          this.$request.get("/pages/selectAll",{
              params : {open : '是'}
          }).then(res =>{
              this.pagesList = res.data || []
          })
      },
  }
}
</script>
