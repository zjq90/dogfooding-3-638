package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}
