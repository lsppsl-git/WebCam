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
@TableName("red_packet")
public class RedPacket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String packetNo;

    private Long senderId;

    private BigDecimal totalAmount;

    private Integer totalCount;

    private BigDecimal remainAmount;

    private Integer remainCount;

    private String message;

    private String type;

    private String status;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt;
}