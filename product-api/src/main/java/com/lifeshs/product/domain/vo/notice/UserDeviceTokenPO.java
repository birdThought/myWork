package com.lifeshs.product.domain.vo.notice;

import lombok.Data;

import java.util.Date;

/**
 *  用户app设备推送表
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年5月17日 下午3:25:32
 */
public @Data class UserDeviceTokenPO {

    private Integer id;
    /** 设备token */
    private String deviceToken;
    /** 用户id */
    private Integer userId;
    /** 操作系统，1_安卓，2_苹果 */
    private Integer OS;
    /** 操作系统版本 */
    private String systemVersion;
    /** 状态 */
    private Integer display;
    /** 创建日期 */
    private Date createDate;

    private int count;
}
