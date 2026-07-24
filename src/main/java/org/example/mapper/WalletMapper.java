package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {

    @Select("SELECT * FROM wallet WHERE user_id = #{userId} FOR UPDATE")
    Wallet findByUserIdForUpdate(@Param("userId") Long userId);

    @Update("UPDATE wallet SET balance = balance - #{amount}, version = version + 1 WHERE user_id = #{userId} AND balance >= #{amount} AND version = #{version}")
    int deduct(@Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount, @Param("version") Integer version);

    @Update("UPDATE wallet SET balance = balance + #{amount}, version = version + 1 WHERE user_id = #{userId} AND version = #{version}")
    int add(@Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount, @Param("version") Integer version);
}