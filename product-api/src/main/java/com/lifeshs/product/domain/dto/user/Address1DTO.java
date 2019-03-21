package com.lifeshs.product.domain.dto.user;

import lombok.Data;

/**
 *  用户地址
 *  @author yuhang.weng
 *  @version 2.4
 *  @DateTime 2017年6月14日 下午3:58:42
 */
public @Data class Address1DTO {

    private Integer id;
    /** 用户id */
    private Integer userId;
    /** 收货人 */
    private String receiverName;
    /** 联系电话 */
    private String contactNumber;
    /** 所在地区 */
    private String address;
    /** 街道 */
    private String street;
    /** 是否为选中的默认值 */
    private Boolean selected;
}
