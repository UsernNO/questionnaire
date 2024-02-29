package com.example.common.enums;

//创建枚举，用于日志信息描述。
public enum LogsTypeEnum {
    LOGIN("登录"),
    REGISTER("注册"),
    UPDATE_PASSWORD("修改密码"),
    ADD("新增"),
    UPDATE("修改"),
    COPU("复制"),
    DELETE("删除");

    private String value;

    public String getValue(){
        return  value;
    }

    LogsTypeEnum(String value){
        this.value = value;
    }
}
