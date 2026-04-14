package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.FaceDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FaceDeviceMapper extends BaseMapper<FaceDevice> {
    
    @Select("SELECT * FROM face_device WHERE device_no = #{deviceNo} AND deleted = 0")
    FaceDevice selectByDeviceNo(@Param("deviceNo") String deviceNo);
    
    @Select("SELECT * FROM face_device WHERE status = 1 AND deleted = 0")
    List<FaceDevice> selectOnlineDevices();
}
