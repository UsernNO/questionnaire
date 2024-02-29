package com.example.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.LogsTypeEnum;
import com.example.common.enums.QuestionTypeEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Resource
    AnswerService answerService;

    @Resource
    QuestionService questionService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            account = adminService.login(account);
        }else if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        }

//        记录日志：
        LogsService.recordLog("登录", LogsTypeEnum.LOGIN.getValue(),account.getUsername());
        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.register(account);
        }else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        }
        //        记录日志：
        LogsService.recordLog("注册", LogsTypeEnum.REGISTER.getValue(),account.getUsername());
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        //        记录日志：
        LogsService.recordLog("修改密码", LogsTypeEnum.UPDATE_PASSWORD.getValue(),account.getUsername());
        return Result.success();
    }

//    数据统计  Dict结构  pageId拿到用户体验的所有问卷id

    /**
     *
     * 数据统计
     * @return
     */
    @GetMapping("/pageCount")
    public  Result pageCount(@RequestParam Integer pageId) {
        Dict pageCount = Dict.create();  //pageCount拿到了所有数据

        //通过answerService 拿到所有的答案 ,answerList去重复
//        answerList.stream() 将 answerList 列表转换为流（Stream），以便进行一系列流式操作。
        List<Answer> answerList = answerService.selectByPageId(pageId);
        long answerCount = answerList.stream().map(Answer::getNo).distinct().count();
        pageCount.set("answerCount",answerCount);

        List<Question> questionList = questionService.selectByPageId(pageId);
//        对questionList 做一个循环。  这个判断用于单选题和多选题，填空题不在范围之内
        for (Question question : questionList) {
            List<String> questionAnswerList = answerList.stream().filter(answer -> answer.getQuestionId().equals(question.getId()))
                    .map(Answer::getContent).collect(Collectors.toList());// 获取用户提交所有回答  questionAnswerList是所有问题的汇总。
            question.setCount(questionAnswerList.size());  //每个问题有多少个回答
            if (QuestionTypeEnum.SINGLE.getValue().equals(question.getType()) || QuestionTypeEnum.MULTIPLE.getValue().equals(question.getType())) {
                List<QuestionItem> questionItemList = question.getQuestionItemList();
                for (QuestionItem questionItem : questionItemList) {
                    long count = questionAnswerList.stream().filter(content -> content.contains(questionItem.getContent())).count();  // 统计当前的这个选项有多少回答,数字包含文本contains
                    questionItem.setCount(count);  //count 是从answerList里面来的  RoundingMode.HALF_UP四舍五入.
                    int percent = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(questionAnswerList.size()) , 2, RoundingMode.UP)
                            .multiply(BigDecimal.valueOf(100)).intValue();  //百分比形势  0.89  -》 89%
                    questionItem.setPercentage(percent);
                }
            } else {
//                转换.
                List<QuestionItem> questionItemList = questionAnswerList.stream().map(content -> {
                    QuestionItem questionItem = new QuestionItem();
                    questionItem.setContent(content);
                    return questionItem;
                }).collect(Collectors.toList());
                question.setQuestionItemList(questionItemList);  //设置填空题的选项集合.
            }
        }
        pageCount.set("questionList",questionList);

        return  Result.success(pageCount);
    }


}
