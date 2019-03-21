package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.domain.po.device.TSportBandStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lifeshs.product.service.common.transform.ICommonTrans;

/**
 *  版权归
 *  TODO 手环-计步 服务类
 *  @author yuhang.weng  
 *  @DateTime 2016年5月26日 下午1:55:36
 */
@Component("bandStep")
public class BandStep{

	@Autowired
	ICommonTrans commonTrans;
	
	/**
	 * 设备类型
	 */
	DeviceType deviceType = DeviceType.BandStep;
	
	public int save(TSportBandStep entity) throws Exception{
		int result = commonTrans.save(entity);
		return result;
	}
	// TODO 查询方法



	
	
	
	
}
