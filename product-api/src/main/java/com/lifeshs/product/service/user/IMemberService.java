package com.lifeshs.product.service.user;

import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.domain.dto.common.AddressDTO;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.domain.dto.user.VcodeDTO;

import java.util.Date;
import java.util.Map;

public interface IMemberService {

    /**
     * 更新用户healthProduct字段
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 下午4:01:31
     *
     * @param id
     * @param healthProductBinaryValue
     */
    void updateUserHealthProduct(int id, int healthProductBinaryValue);



    /**
     *  添加睡眠记录
     *  @author yuhang.weng
     *	@DateTime 2017年3月21日 下午2:20:14
     *
     *  @param userId
     *  @param hour
     */
    void addSleepHourRecord(int userId, float hour);


    /**
     * 添加血氧记录
     *
     * @author yuhang.weng
     * @DateTime 2017年3月18日 下午2:33:52
     *
     * @param userId
     * @param saturationStatus
     * @param heartRate 心率
     * @param heartRateStatus 心率是否异常
     */
    void addOxygenRecord(int userId, Integer saturation, Boolean saturationStatus, Integer heartRate, Boolean heartRateStatus);


    /**
     * 添加肺活仪分数记录
     *
     * @author yuhang.weng
     * @DateTime 2017年3月18日 下午2:37:44
     *
     * @param userId
     * @param vitalCapacityScore
     */
    void addLunginstrumentRecord(int userId, Integer vitalCapacity, Integer vitalCapacityScore);


    /**
     *  添加血压记录
     *
     * @author yuhang.weng
     * @DateTime 2017年3月18日 下午2:33:27
     *
     *  @param userId
     *  @param diastolic
     *  @param systolic
     *  @param bloodPressureStatus
     *  @param heartRate
     *  @param heartRateStatus
     */
    void addBloodPressureRecord(int userId, Integer diastolic, Integer systolic, Boolean bloodPressureStatus,
                                Integer heartRate, Boolean heartRateStatus);


    /**
     * 添加体脂秤记录
     *
     * @author yuhang.weng
     * @DateTime 2017年3月18日 下午2:37:57
     *
     * @param userId
     * @param WHRStatus
     * @param BMIRankStatus
     * @param baseMetabolismStatus
     * @param weight
     */
    void addBodyfatscaleRecord(int userId, Float WHR, Boolean WHRStatus, Float BMI, Integer BMIRankStatus,
                               Float baseMetabolism, Boolean baseMetabolismStatus, Float weight);


    /**
     * 获取一个用户
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 下午2:52:00
     *
     * @param id
     * @return
     */
    MemberUserDTO getUser(Integer id);

    /**
     * 获取用户个人档案
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 上午11:38:40
     *
     * @param userId
     * @return
     */
    UserRecordDTO getRecord(int userId);

    /**
     * @author wenxian.cai
     * @DateTime 2017-4-1
     * @param sendId
     * @return
     * 判断用户当天的短信发送数量是否超出范围
     */
    boolean filterIllegalMobileNumber(int sendId, String userName);

    /**
     * @Description: 添加验证码信息
     * @author: wenxian.cai
     * @create: 2017/4/18 14:42
     */
    boolean addVcode(VcodeDTO vcodeDTO);

    /**
     * <p>
     * 修改用户手机号码
     *
     * @author dachang.luo
     * @DateTime 2016年6月8日下午4:24:20
     *
     * @param id
     * @param mobile
     * @return
     */
    boolean updateMobile(int id, String mobile);

    /**
     * 根据id获取用户(关联 表: t_user_oauth)
     * @param userId
     * @return
     * @author liu
     * @时间 2018年12月26日 上午11:34:51
     * @remark
     */
    Map<String, Object> getUserByUserId(Integer userId);

    /**
     *
     * @author yuhang.weng
     * @DateTime 2016年6月8日 上午10:00:40
     * @serverComment 重置密码
     *
     * @param userId 用户ID
     * @param password 新密码
     * @param ip ip
     * @return
     */
    String modifyPasswordByUserId(String userId, String password, String ip);

    /**
     *
     * @author dachang.luo
     * @DateTime 2016-5-13 下午03:05:00
     * @serverComment 根据手机判断用户是否存在(手机必须是已验证）
     *
     * @param mobile
     *            手机号
     * @return userId
     * @throws Exception
     */
    String checkMobile(String mobile);

    /**
     * 更新用户基本信息
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 下午3:33:32
     *
     * @param id
     * @param birthday
     * @param gender
     * @param realName
     * @param photo
     * @param address
     */
    void updateUserBaseInfo(int id, Date birthday, Boolean gender, String realName, String photo, AddressDTO address);

    /**
     * 修改用户个人档案信息
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 下午5:01:50
     *
     * @param userId
     * @param height
     * @param weight
     * @param waist
     * @param bust
     * @param hip
     */
    void updateUserRecord(int userId, Float height, Float weight, Float waist, Float bust, Float hip);

    /**
     *  更新用户异常项
     *  @author yuhang.weng
     *	@DateTime 2017年3月21日 下午2:44:10
     *
     *  @param userId
     *  @param hasWarning
     */
    void updateHasWarning(int userId, Integer hasWarning);

    /**
     * 存储用户测量设备异常信息
     * @param userId 用户id
     * @param healthPackageType 设备类型
     * @param measureDate 测量时间
     * @exception OperationException
     * @return
     */
    /*void saveHealthpackageWarning(int userId, HealthPackageType healthPackageType, Date measureDate) throws OperationException;
*/
}
