package com.lifeshs.product.service.notice.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.exception.base.BaseException;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.dao.notice.IUserDeviceTokenDao;
import com.lifeshs.product.domain.vo.notice.UserDeviceTokenPO;
import com.lifeshs.product.service.notice.IPushDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service(value = "pushDataService")
public class PushDataServiceImpl implements IPushDataService {

    @Resource(name = "userPushDeviceTokenDao")
    private IUserDeviceTokenDao userDeviceTokenDao;

    @Override
    public UserDeviceTokenPO getUserPushToken(int userId) {
        return userDeviceTokenDao.findDeviceTokenByUserId(userId);
    }
    
    @Override
    public List<UserDeviceTokenPO> findUserDeviceTokenPOList(String projectCode, Integer diseasesId, Integer gender, String startAge, String endAge, String mobile, Integer orgId) {
        return userDeviceTokenDao.findUserDeviceTokenPOList(projectCode, diseasesId, gender, startAge, endAge, mobile, orgId);
    }

    @Override
    public void addUserPushToken(UserDeviceTokenPO token) throws BaseException {
        Integer userId = token.getUserId();
        String deviceToken = token.getDeviceToken();
        if (userId == null) {
            throw new ParamException("用户id不允许为空");
        }
        if (StringUtils.isBlank(deviceToken)) {
            throw new ParamException("设备token不允许为空");
        }
        
        UserDeviceTokenPO po = userDeviceTokenDao.findDeviceToken(userId,token.getOS(),deviceToken,token.getSystemVersion());
        //存在记录
        if(po != null){
            //不是同一台设备
            if(po.getDisplay() ==0){
                userDeviceTokenDao.delDeviceTokenByUserId(userId);
                userDeviceTokenDao.updateTokenByUserId(userId,token.getOS(),deviceToken,token.getSystemVersion(), 1);
            }
        }else{
            List<UserDeviceTokenPO> poList = userDeviceTokenDao.findDeviceTokenList(userId);
            //不存在记录
            if(poList.size()>0){
                userDeviceTokenDao.delDeviceTokenByUserId(userId);
            }
            userDeviceTokenDao.addDeviceToken(token);
        }
    }

    @Override
    public void deleteUserPushToken(int userId) throws OperationException {
        int result = userDeviceTokenDao.delDeviceTokenByUserId(userId);
        if (result == 0) {
            throw new OperationException("删除token失败", ErrorCodeEnum.FAILED);
        }
    }


    @Override
    public List<UserDeviceTokenPO> getUserPushToken(List<Integer> userIdList) {
        List<UserDeviceTokenPO> dataList = new ArrayList<>();
        if (userIdList.size() > 0) {
            dataList = userDeviceTokenDao.findDeviceTokenByUserIdList(userIdList);
        }
        return dataList;
    }
}
