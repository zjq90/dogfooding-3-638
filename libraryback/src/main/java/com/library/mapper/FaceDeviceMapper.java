package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.FaceDevice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface FaceDeviceMapper extends BaseMapper<FaceDevice> {

    @Select("SELECT * FROM face_device WHERE deleted = 0 ORDER BY create_time DESC")
    List<FaceDevice> selectAllDevices();

    @Select("SELECT * FROM face_device WHERE status = #{status} AND deleted = 0")
    List<FaceDevice> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM face_device WHERE device_no = #{deviceNo} AND deleted = 0")
    FaceDevice selectByDeviceNo(@Param("deviceNo") String deviceNo);

    @Update("UPDATE face_device SET last_heartbeat = #{heartbeatTime}, status = 1 WHERE id = #{deviceId}")
    int updateHeartbeat(@Param("deviceId") Long deviceId, @Param("heartbeatTime") LocalDateTime heartbeatTime);
}
