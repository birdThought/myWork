package com.lifeshs.product.service.notice;

import com.lifeshs.product.common.exception.base.BaseException;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.domain.vo.notice.UserDeviceTokenPO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  推送数据相关服务
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年8月16日 下午3:12:07
 */
@Service
public interface IPushDataService {

    /**
     *  获取用户的推送设备token
     *  @author yuhang.weng 
     *  @DateTime 2017年8月16日 下午5:38:55
     *
     *  @param userId
     *  @return
     */
    UserDeviceTokenPO getUserPushToken(int userId);
    
    /**
     * 
     *  根据查询条件获取用户的推送设备token
     *  @author liaoguo
     *  @DateTime 2018年5月18日 上午11:56:25
     *
     *  @param projectCode
     *  @param diseasesId
     *  @param gender
     *  @param startAge
     *  @param endAge
     *  @param mobile
     *  @param orgId
     *  @return
     */
    List<UserDeviceTokenPO> findUserDeviceTokenPOList(String projectCode, Integer diseasesId, Integer gender, String startAge, String endAge, String mobile, Integer orgId);
    
    /**
     *  获取用户的推送设备token
     *  @author yuhang.weng 
     *  @DateTime 2017年8月31日 上午10:52:38
     *
     *  @param userIdList
     *  @return
     */
    List<UserDeviceTokenPO> getUserPushToken(List<Integer> userIdList);
    
    /**
     *  添加用户的推送设备token
     *  @author yuhang.weng 
     *  @DateTime 2017年8月16日 下午5:39:08
     *
     *  @param token
     *  @return
     *  @throws DataBaseException
     */
    void addUserPushToken(UserDeviceTokenPO token) throws BaseException;
    
    /**
     *  删除用户的推送设备token
     *  @author yuhang.weng 
     *  @DateTime 2017年8月16日 下午5:39:27
     *
     *  @param userId
     *  @return
     */
    void deleteUserPushToken(int userId) throws OperationException;
}
