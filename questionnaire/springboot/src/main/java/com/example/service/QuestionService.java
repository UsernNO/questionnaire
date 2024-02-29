package com.example.service;

import com.example.common.enums.QuestionTypeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Question;
import com.example.entity.QuestionItem;
import com.example.mapper.QuestionMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目表业务处理
 **/
@Service
public class QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    QuestionItemService questionItemService;

    /**
     * 新增
     */
    public void add(Question question) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            question.setUserId(currentUser.getId()); //设置一个userId进去
        }
        questionMapper.insert(question);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        questionMapper.deleteById(id);

//        删除时删除t题目选项
        questionItemService.deleteQuestionItemById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Question question) {
        questionMapper.updateById(question);
    }

    /**
     * 根据ID查询
     */
    public Question selectById(Integer id) {
        return questionMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Question> selectAll(Question question) {
        return questionMapper.selectAll(question);
    }

    /**
     * 分页查询
     */
    public PageInfo<Question> selectPage(Question question, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Question> list = questionMapper.selectAll(question);
        return PageInfo.of(list);
    }

    public List<Question> selectByPageId(Integer pageId) {
        List<Question> questionList = questionMapper.selectByPageId(pageId);
        for (Question question : questionList) {
            List<QuestionItem> questionItemList = questionItemService.selectByQuestionId(question.getId());
            question.setQuestionItemList(questionItemList);
        }

        return  questionList;
    }

//    插入新题目，用户新建题目
    @Transactional  //两个新增使用在一起的话就要价格事务
    public void addForUser(Question question) {
        this.add(question);

        Integer questionId = question.getId();
        //单选和多选需要默认一个选项。
        if (QuestionTypeEnum.SINGLE.getValue().equals(question.getType()) || QuestionTypeEnum.MULTIPLE.getValue().equals(question.getType())) {
            QuestionItem questionItem1 = new QuestionItem();
            questionItem1.setQuestionId(questionId); //关联
            questionItemService.add(questionItem1); //插入数据库

            QuestionItem questionItem2 = new QuestionItem();
            questionItem2.setQuestionId(questionId);
            questionItemService.add(questionItem2);
        }
    }

    public void deletePageId(Integer pageId) {
        //根据问卷ID删除题目。  查出所有题目  根据pageId删除所有题目
        List<Question> questionList = this.selectByPageId(pageId);//根据pageId查询所有题目
        questionMapper.deletePageId(pageId);   //定义方法，根据问卷 删除所有题目
//        删除题目选项
        for (Question question : questionList) { //题目循环，删除需要的题目
            questionItemService.deleteQuestionItemById(question.getId());   //查询删除题目信息。
        }
    }
}