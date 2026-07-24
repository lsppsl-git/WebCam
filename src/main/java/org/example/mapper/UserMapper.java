package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE is_login = TRUE")
    List<User> findOnlineUsers();

    @Update("UPDATE users SET is_login = #{isLogin} WHERE id = #{id}")
    void updateLoginStatus(@Param("id") Long id, @Param("isLogin") Boolean isLogin);
}