package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("transaction_log")
public class TransactionLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String transactionNo;

    private Long fromUserId;

    private Long toUserId;

    private BigDecimal amount;

    private String type;

    private String status;

    private String remark;

    private LocalDateTime createdAt;
}