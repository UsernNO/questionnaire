<template>
    <div class="main-content">
        <div class="card">
            <div style="display: flex; margin-bottom: 20px" >
                <div style="flex:1">
                    <span style="font-size: 24px;margin-right: 10px"> {{pages.name}}</span>
                    <el-tag v-if="pages.saved === '否'">未发布</el-tag>
                    <el-tag v-else type="success">已发布</el-tag>
                </div>
                <div>
                    <el-button type="primary" @click="share"><i class="el-icon-share" style="margin-right: 10px"></i> 分享</el-button>
                    <el-button type="info" @click="preview(pages.id)"><i class="el-icon-video-play" style="margin-right: 10px"></i> 预览</el-button>
                    <el-button type="success" :disabled="pages.saved === '是'" @click="handleSaved"><i class="el-icon-s-promotion" style="margin-right: 10px"></i> 发布</el-button>
                </div>
            </div>

            <div style="margin-bottom: 20px">
                <el-button type="primary" @click="addQuestion('单选题')">新建单选题</el-button>
                <el-button type="info" @click="addQuestion('多选题')">新建多选题</el-button>
                <el-button type="success" @click="addQuestion('填空题')">新建填空题</el-button>
            </div>

            <div>
                <div v-for="(item,index) in questionList" :key="item.id" style="margin-bottom:15px">
                    <div>
                        <span style="font-weight: bold;margin-right:10px">题目{{index + 1}}</span>
                        <el-input v-model="item.name" style="width: 70%;margin-right: 10px" @change="changeQuestionName(item)"></el-input>
                        <el-tag style="margin-right: 5px; color: #df4141;" v-if="item.type === '单选题'">{{item.type}}</el-tag>
                        <el-tag style="margin-right: 5px; color: #df4141;" v-if="item.type === '多选题'">{{item.type}}</el-tag>
                        <el-tag style="margin-right: 5px; color: #ffae35;" v-if="item.type === '填空题'">{{item.type}}</el-tag>
                        <span style="color: #8c939d; cursor: pointer;  font-size: 12px" @click="delQuestion(item.id)"><i class="el-icon-delete"></i>删除</span>
                    </div>

                    <div style="padding-left: 40px"v-if="item.questionItemList?.length">
                        <div v-for="(sub,subIndex) in item.questionItemList" :key="sub.id" style="margin: 10px 0">
                            <div style="padding-left:40px;">
                                <span>选项{{subIndex + 1}}</span>
                                <el-input v-model="sub.content" style="width: 50%" @change="changeQuestionItem(sub)"></el-input>
                                <span style="color: #8c939d; cursor: pointer;  font-size: 12px;margin-left: 5px" @click="delQuestionItem(sub.id)"><i class="el-icon-delete"></i>删除</span>
                            </div>
                        </div>
                        <div style="padding-left:40px;">
                            <span style="margin-right: 5px;color: #2a60c9">新选项</span>
                            <el-input v-model="item.newContent"  style="width: 50%; margin-right: 5px"></el-input>
                            <el-button style="background-color: #df4141;color: white" @click="addQuestionItem(item)">确认添加</el-button>
                        </div>
                    </div>
                </div>
            </div>
            <div>
            <el-button style="margin-left: 95%;margin-top: 20px;color: #white" type="success" @click="Close">关 闭</el-button>
        </div>
        </div>
        <el-dialog title="分享" :visible.sync="shareVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
                <div style="display: flex;align-items: center" >
                    <span style="margin-left: 5px">复制链接： </span> <el-input v-model="link" style="flex:1"></el-input>
                    <el-button type="primary" style="margin-left: 5px" @click="copyLink">复制</el-button>
                </div>

            <div slot="footer" class="dialog-footer">
                <el-button @click="shareVisible = false">关 闭</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import QuestionItem from "../manager/QuestionItem";

    export default {
        name: "Design",
        data(){
            return{
                pages:{},
                questionList:[],
                shareVisible: false,
                link : ' '
            }
        },
        created() {
            this.load();
        },
        methods:{
            //关闭按钮
            Close() {
                if (!this.questionList.length) {
                    this.$message.warning("信息将不做保存,您确定要关闭嘛?")
                }
              window.close();

            },
            //复制链接，通过js手段放到
            copyLink() {
                let _input = document.createElement("input");   // 直接构建input

                _input.value = this.link;  // 设置内容
                document.body.appendChild(_input);    // 添加临时实例
                _input.select();   // 选择实例内容
                document.execCommand("Copy");   // 执行复制
                document.body.removeChild(_input)
                this.$message.success("复制成功")
            },
            //分享
            share() {
                if (this.pages.saved !== '是') {
                    this.$message.warning("未公开的问卷不允许分享");
                    return
                }
                //0, location.href.indexOf('/front')链接地址，从0开始到front结束。
                this.link = location.href.substring(0, location.href.indexOf('/front')) + '/realPage?pageId=' + this.pages.id;
                this.shareVisible = true  //打开公开
            },
            //确认添加选项
            addQuestionItem(question) {
                this.$request.post('/questionItem/add/',  { questionId: question.id, content: question.newContent }).then(res => {
                    if (res.code === '200') {  // 表示成功保存
                        this.$message.success('操作成功')
                        this.load()
                    } else {
                        this.$message.error(res.msg)  // 弹出错误的信息
                    }
                })
            },
            //新增
            addQuestion(type) {
                this.$request.post('/question/addForUser', {name: '', type: type, pageId: this.pages.id}).then(res => {
                    if (res.code === '200') {  // 表示成功保存
                        this.$message.success('操作成功');
                        this.load()
                    } else {
                        this.$message.error(res.msg)  // 弹出错误的信息
                    }
                })
            },
            //更新数据，需要把date数据传入后台
            changeQuestionName(question){
                let data = JSON.parse(JSON.stringify(question));  //编译question
                this.$request.put('/question/update/',data).then(res=>{
                    if(res.code === '200'){
                        this.$message.success('更新成功');
                        this.load();  //重启
                    }else{
                        this.$message.error(res.msg) //弹出错误
                    }
                })
            },
            //更新选项信息
            changeQuestionItem(questionItem){
                let  data = JSON.parse(JSON.stringify(questionItem));
                this.$request.put('/questionItem/update/',data).then(res =>{
                    if(res.code === '200'){
                        this.$message.success("更新成功")
                        this.load();
                    }else{
                        this.$message.error(res.msg)
                    }
                })
            },
            load(){
                let  pageId = this.$route.query.pageId; //这个方式拿到home传的值
                this.$request.get('/pages/selectById/' + pageId).then(res =>{ //注意Id后面需要添加/否则报错404
                    this.pages = res.data || {}
                })
                //获取题目信息。
                this.$request.get('/question/selectByPageId/',{
                    params:{
                        pageId:pageId
                    }
                }).then(res=>{
                    this.questionList = res.data || []
                })
            },
            delQuestion(questionId){  //单个删除
                this.$confirm('确定要删除吗？','确认删除',{type:"warning"}).then(response =>{
                    this.$request.delete('/question/delete/' + questionId).then(res =>{
                        if(res.code === '200'){
                            this.$message.success('删除成功');
                            this.load()
                        }else{
                            this.$message.error(res.msg)
                        }
                    })
                }).catch(()=>{
                })
            },
            delQuestionItem(questionItemId) {   // 选项删除
                this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
                    this.$request.delete('/questionItem/delete/' + questionItemId).then(res => {
                        if (res.code === '200') {   // 表示操作成功
                            this.$message.success('删除成功');
                            this.load()
                        } else {
                            this.$message.error(res.msg)  // 弹出错误的信息
                        }
                    })
                }).catch(() => {
                })
            },
            //发布
            handleSaved(){
                if (!this.valuedata()) {  //校验不通过
                    return
                }
                this.$confirm('您确定要发布吗？','确定发布',{type: "warning"}).then(response =>{
                    let data = JSON.parse(JSON.stringify(this.pages));
                    data.saved = '是';
                    this.$request.put('/pages/update/' , data).then(res =>{
                        if (res.code === '200') {
                            this.$message.warning('发布成功');
                            this.load();
                        }else {
                            this.$message.error(res.msg) //弹出错误消息
                        }
                    })
                }).catch(err => console.log(err))
            },
            //统一校验
            valuedata(){
                if (!this.questionList.length) {
                    this.$message.warning("您为添加任何题目信息,无法发布")
                    return  false
                }
                let flag = false; // 检查内容是否为空  当flag = true 的时候就是空的
                for (let i = 0; i < this.questionList.length; i++) {
                    if (this.questionList[i].type === '单选题' ||  this.questionList[i].type === '多选题') {
                        if (!this.questionList[i].questionItemList?.length) {
                            this.$message.warning('单选题和多选题需要添加选项');
                            return false
                        }
                        this.questionList[i].questionItemList.forEach(item => {
                            if (!item.content) {
                                flag = true
                            }
                        })
                    }
                    if (!this.questionList[i].name) {
                        this.$message.warning('请填写题目名称');
                        return false
                    }
                }
                if (flag) {
                    this.$message.warning('请完善选项内容');
                    return false
                }
                return  true
            },
            //问卷预览功能
            preview(pageId) {
                if (this.valuedata()) {  //校验通过   调用函数。
                    window.open('/front/preview?pageId=' + pageId)
                }
            },
        }
    }
</script>

<style scoped>

</style>