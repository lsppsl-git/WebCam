package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    @Select("SELECT * FROM file_info WHERE md5 = #{md5} AND status = 'COMPLETED'")
    FileInfo findByMd5(@Param("md5") String md5);

    @Select("SELECT * FROM file_info WHERE upload_id = #{uploadId}")
    FileInfo findByUploadId(@Param("uploadId") String uploadId);
}