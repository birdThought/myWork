package com.lifeshs.product.domain.vo.user;

import lombok.Data;

import java.util.Date;

public @Data
class UserMeasurePO{
    private Integer id; //用户id
    private Date date; //时间
    private Integer isRead; //默认0 未读
    private Integer device; //设备码
    private String deviceName; //设备名
    private Integer diseasesId; //病种id
    private String diseasesName; //病种名字
    /** 用户登录名 */
    private String userName;
    /** 真实姓名 */
    private String realName;
}
