package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT * FROM messages WHERE from_user_id = #{userId} OR to_user_id = #{userId} ORDER BY timestamp DESC LIMIT #{limit}")
    List<Message> findMessagesByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    @Select("SELECT * FROM messages WHERE (from_user_id = #{user1} AND to_user_id = #{user2}) OR (from_user_id = #{user2} AND to_user_id = #{user1}) ORDER BY timestamp DESC LIMIT #{limit}")
    List<Message> findPrivateMessages(@Param("user1") Long user1, @Param("user2") Long user2, @Param("limit") Integer limit);
}