package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.LogsTypeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Pages;
import com.example.entity.Question;
import com.example.entity.QuestionItem;
import com.example.mapper.PagesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 问卷表业务处理
 **/
@Service
public class PagesService {

    @Resource
    private PagesMapper pagesMapper;

    @Resource
    QuestionService questionService;

    @Resource
    QuestionItemService questionItemService;


    /**
     * 新增
     */
    public void add(Pages pages) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            pages.setUserId(currentUser.getId());
        }
        pages.setCreateTime(DateUtil.now());    //延迟时间
        pagesMapper.insert(pages);

        //        记录日志：
        LogsService.recordLog("创建问卷【" + pages.getName() + "】", LogsTypeEnum.ADD.getValue(), currentUser.getUsername());
    }

    /**
     * 删除
     */
//    @Transactional
    public void deleteById(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        Pages pages = this.selectById(id);
        pagesMapper.deleteById(id);//位置不对无法删除

        questionService.deletePageId(id);


        //        记录日志：
        LogsService.recordLog("创建问卷【" + pages.getName() + "】", LogsTypeEnum.DELETE.getValue(), currentUser.getUsername());
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {//调用删除方法
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Pages pages) {
        Account currentUser = TokenUtils.getCurrentUser();
        pagesMapper.updateById(pages);
        //        记录日志：
        LogsService.recordLog("修改问卷【" + pages.getName() + "】", LogsTypeEnum.UPDATE.getValue(), currentUser.getUsername());
    }

    /**
     * 根据ID查询
     */
    public Pages selectById(Integer id) {
        return pagesMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Pages> selectAll(Pages pages) {
        return pagesMapper.selectAll(pages);
    }

    /**
     * 分页查询
     */
    public PageInfo<Pages> selectPage(Pages pages, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();  //拿到当前登录的用户信息
        if(RoleEnum.USER.name().equals(currentUser.getRole())){   // 如果是用户，需要筛选出当前用户自己的问卷信息
            pages.setUserId(currentUser.getId());  //返回用户的信息出现

        }
        PageHelper.startPage(pageNum, pageSize);
        List<Pages> list = pagesMapper.selectAll(pages);
        return PageInfo.of(list);
    }
    //    复制
    @Transactional //添加事务，
    public Integer copy(Integer pageId) {
        Pages pages = this.selectById(pageId); //拿到当前被拷贝的问卷内容
        pages.setCount(pages.getCount() + 1);  //更新使用模板的次数
        this.updateById(pages);  //更新使用次数

        Pages newPage = new Pages(); //复制一个新的，拿出当前要拿的内容
        newPage.setName(pages.getName() + "—— —— 新");
//        Account currentUser = TokenUtils.getCurrentUser();
//        newPage.setUserId(currentUser.getId());//!!!!复制之后的问卷应该使用新的用户id
        newPage.setDescr(pages.getDescr());
        newPage.setImg(pages.getImg());
        this.add(newPage);

        List<Question> questionList = questionService.selectByPageId(pageId); //拿到当前问卷所关联的题目信息。包括内容
        for (Question question : questionList) {
            question.setId(null);  //清除原先的主键，要插入新的数据。
            question.setPageId(newPage.getId()); //设置题目问卷id
            questionService.add(question);


            List<QuestionItem> questionItemList = question.getQuestionItemList();
            for (QuestionItem questionItem : questionItemList) {
                questionItem.setId(null);
                questionItem.setQuestionId(question.getId());
                questionItemService.add(questionItem);
            }
        }
        LogsService.recordLog("复制新的问卷【" + newPage.getName() + "】", LogsTypeEnum.COPU.getValue(), TokenUtils.getCurrentUser().getUsername());

        return  newPage.getId(); //返回最新的一个页面id
    }
}