package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("face_device")
public class FaceDevice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceNo;

    private String deviceName;

    private String deviceType;

    private String location;

    private String ipAddress;

    private Integer status;

    private LocalDateTime lastHeartbeat;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;

    @TableField(exist = false)
    private String statusName;
}
