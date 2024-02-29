package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserMapper {

    List<User> selectAll(User user);

    void insert(User user);

    void deleteById(Integer id);

    void updateById(User user);

    User selectById(Integer id);

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);
}
