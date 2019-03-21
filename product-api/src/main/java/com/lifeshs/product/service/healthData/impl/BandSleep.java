package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.domain.po.device.TSportBandSleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lifeshs.product.service.common.transform.ICommonTrans;

/**
 *  版权归
 *  TODO 手环-睡眠 服务类
 *  @author yuhang.weng  
 *  @DateTime 2016年5月26日 下午1:56:22
 */
@Component
public class BandSleep {
	
	@Autowired
	ICommonTrans commonTrans;
	
	/**
	 * 设备类型
	 */
	DeviceType deviceType = DeviceType.BandSleep;
	
	public int save(TSportBandSleep entity) throws OperationException {
		int result = commonTrans.save(entity);
		return result;
	}
	
	// TODO 查询方法
	
}
