package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.TransactionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLog> {

    @Select("SELECT * FROM transaction_log WHERE transaction_no = #{transactionNo}")
    TransactionLog findByTransactionNo(@Param("transactionNo") String transactionNo);

    @Select("SELECT * FROM transaction_log WHERE from_user_id = #{userId} OR to_user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<TransactionLog> findByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
}