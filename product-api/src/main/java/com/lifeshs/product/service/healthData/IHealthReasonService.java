package com.lifeshs.product.service.healthData;

import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.common.constants.healthData.HealthRank;
import com.lifeshs.product.common.constants.healthData.HealthType;
import com.lifeshs.product.domain.dto.healthData.ReasonDTO;

import java.util.List;


/**
 *  形成原因Service
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年3月14日 上午11:26:45
 */
public interface IHealthReasonService {

    /**
     *  获取一条异常形成原因
     *  @author yuhang.weng 
     *	@DateTime 2017年3月14日 下午1:45:36
     *
     *  @param healthType
     *  @param rank
     *  @param professional 是否为专业信息
     *  @return
     */
    String getReason(HealthType healthType, HealthRank rank, boolean professional);
    
    /**
     *  获取形成原因集合
     *  @author yuhang.weng 
     *	@DateTime 2017年3月14日 下午2:36:34
     *
     *  @param packageType
     *  @param professional 是否为专业信息
     *  @return
     */
    List<ReasonDTO> listReason(DeviceType packageType, boolean professional);
}
