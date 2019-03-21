package com.lifeshs.product.component.umeng.util;

import lombok.Data;

/**
 *  返回结果
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年5月22日 下午2:03:06
 */
public @Data class CallBackDTO {

    private String ret;
    
    private CallBackDataDTO data = new CallBackDataDTO();

}
