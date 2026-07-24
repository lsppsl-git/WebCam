package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.RedPacketRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RedPacketRecordMapper extends BaseMapper<RedPacketRecord> {

    @Select("SELECT * FROM red_packet_record WHERE red_packet_id = #{packetId} AND user_id = #{userId}")
    RedPacketRecord findByPacketAndUser(@Param("packetId") Long packetId, @Param("userId") Long userId);

    @Select("SELECT * FROM red_packet_record WHERE red_packet_id = #{packetId} ORDER BY created_at DESC")
    List<RedPacketRecord> findByPacketId(@Param("packetId") Long packetId);

    @Select("SELECT COUNT(*) FROM red_packet_record WHERE red_packet_id = #{packetId}")
    int countByPacketId(@Param("packetId") Long packetId);
}