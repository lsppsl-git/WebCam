package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("messages")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String msgId;

    private String type;

    private Long fromUserId;

    private Long toUserId;

    private String content;

    private String status;

    private LocalDateTime timestamp;

    private LocalDateTime createdAt;
}