package com.lifeshs.product.domain.dto.common;

import lombok.Data;

/**
 *  用户app设备推送表
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年5月17日 下午3:25:32
 */
public @Data class PushDataDTO {

    private Integer id;
    
    private String deviceToken;
    
    private Integer userId;
    
    private Integer OS;
    
    private String systemVersion;
}
