package com.example.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 题目表
 */
//接口
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 题目名称 */
    private String name;
    /** 题目类型 */
    private String type;
    /** 问卷ID */
    private Integer pageId;
    /** 创建人ID */
    private Integer userId;

    private  String pageName;

    private  String userName;

    //用户填写的数量
    private  Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    //    /** 问卷关联问题 */
//    存储 QuestionItem 对象的列表
//    通过将 QuestionItem 对象添加到 questionItemList 中，可以创建一个包含多个问题的列表。
    private  List<QuestionItem> questionItemList;

    public List<QuestionItem> getQuestionItemList() {
        return questionItemList;
    }

    public void setQuestionItemList(List<QuestionItem> questionItemList) {
        this.questionItemList = questionItemList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}