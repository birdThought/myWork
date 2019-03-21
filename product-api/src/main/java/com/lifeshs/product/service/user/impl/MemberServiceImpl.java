package com.lifeshs.product.service.user.impl;

import com.lifeshs.product.common.constants.base.SensitiveOperationType;
import com.lifeshs.product.common.constants.base.UserType;
import com.lifeshs.product.utils.MD5Utils;
import com.lifeshs.product.dao.user.IMemberDao;
import com.lifeshs.product.domain.dto.common.AddressDTO;
import com.lifeshs.product.domain.dto.user.SensitiveOperationLogDTO;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.domain.dto.user.VcodeDTO;
import com.lifeshs.product.service.user.IMemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 版权归 TODO
 *
 * @author duosheng.mo
 * @DateTime 2016年4月20日 上午9:41:03 extends CommonDaoImpl
 */
@Service("memberService")
public class MemberServiceImpl implements IMemberService {



    @Autowired
    private Member member;

    @Autowired
    private IMemberDao memberDao;

    @Override
    public void updateUserHealthProduct(int id, int healthProductBinaryValue) {
        MemberUserDTO memberUserDTO = new MemberUserDTO();
        memberUserDTO.setId(id);
        memberUserDTO.setHealthProduct(healthProductBinaryValue);
        memberDao.updateUser(memberUserDTO);
    }

    @Override
    public void addSleepHourRecord(int userId, float hour) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        recordDTO.setSleepHour(hour);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public  boolean filterIllegalMobileNumber(int sendId, String userName) {

        return memberDao.getIllegalMobileNumber(sendId, userName) <= 10 ? true : false;
    }

    @Override
    public void addOxygenRecord(int userId, Integer saturation, Boolean saturationStatus, Integer heartRate, Boolean heartRateStatus) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        recordDTO.setSaturation(saturation);
        recordDTO.setSaturationStatus(saturationStatus);
        recordDTO.setHeartRate(heartRate);
        recordDTO.setHeartRateStatus(heartRateStatus);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public void addLunginstrumentRecord(int userId, Integer vitalCapacity, Integer vitalCapacityScore) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        recordDTO.setVitalCapacity(vitalCapacity);
        recordDTO.setVitalCapacityScore(vitalCapacityScore);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public void addBloodPressureRecord(int userId, Integer diastolic, Integer systolic, Boolean bloodPressureStatus,
                                       Integer heartRate, Boolean heartRateStatus) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        recordDTO.setDiastolic(diastolic);
        recordDTO.setSystolic(systolic);
        recordDTO.setBloodPressureStatus(bloodPressureStatus);
        recordDTO.setHeartRate(heartRate);
        recordDTO.setHeartRateStatus(heartRateStatus);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public void addBodyfatscaleRecord(int userId, Float WHR, Boolean WHRStatus, Float BMI, Integer BMIRankStatus,
                                      Float baseMetabolism, Boolean baseMetabolismStatus, Float weight) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        if (WHR != null && WHR != 0) {
            recordDTO.setWHR(WHR);
            recordDTO.setWHRStatus(WHRStatus);
        }
        if (BMI != null && BMI != 0) {
            recordDTO.setBMI(BMI);
            recordDTO.setBMIRankStatus(BMIRankStatus);
        }
        if (baseMetabolism != null && baseMetabolism != 0) {
            recordDTO.setBaseMetabolism(baseMetabolism);
            recordDTO.setBaseMetabolismStatus(baseMetabolismStatus);
        }
        recordDTO.setWeight(weight);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public MemberUserDTO getUser(Integer id) {

        return memberDao.getUser(id);
    }

    @Override
    public UserRecordDTO getRecord(int userId) {
        return memberDao.getUserRecord(userId);
    }

    @Override
    public boolean addVcode(VcodeDTO vcodeDTO) {
        return memberDao.addVcode(vcodeDTO) > 0 ? true : false;
    }

    @Override
    public boolean updateMobile(int id, String mobile) {
        if (StringUtils.isBlank(checkMobile(mobile))) {
            MemberUserDTO memberUserDTO = new MemberUserDTO();
            memberUserDTO.setId(id);
            memberUserDTO.setMobile(mobile);
            memberUserDTO.setMobileVerified(true); // true 表示已验证
            memberDao.updateUser(memberUserDTO);
            return true;
        }
        return false;
    }

    @Override
    public String checkMobile(String mobile) {
        String userId = null;
        if (member.checkMobile(mobile)) {
            MemberUserDTO user = memberDao.getUserByMobile(mobile);
            if (user != null) {
                userId = String.valueOf(user.getId());
            }
        }
        return userId;
    }

    @Override
    public Map<String, Object> getUserByUserId(Integer userId) {
        return memberDao.getUserByUserId(userId);
    }

    @Override
    public String modifyPasswordByUserId(String userId, String password, String ip) {
        MemberUserDTO user = memberDao.getUser(Integer.parseInt(userId));
        // 查找不到用户信息直接返回null
        if (user == null) {
            return null;
        }

        String newPassword = MD5Utils.encryptPassword(password);
        MemberUserDTO memberUserDTO = new MemberUserDTO();
        memberUserDTO.setId(user.getId());
        memberUserDTO.setPassword(newPassword);
        memberDao.updateUser(memberUserDTO);

        SensitiveOperationLogDTO log = new SensitiveOperationLogDTO();
        log.setGenerateData(user.getPassword());
        log.setNewData(MD5Utils.encryptPassword(password));
        log.setUserId(user.getId());
        log.setUserType(UserType.member);
        log.setOperationType(SensitiveOperationType.MODIFY_PASSWORD);
        log.setIp(ip);
        member.saveSensitiveLog(log);

        return user.getUserName();
    }

    @Override
    public void updateUserBaseInfo(int id, Date birthday, Boolean gender, String realName, String photo,
                                   AddressDTO address) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(id);
        recordDTO.setBirthday(birthday);
        recordDTO.setGender(gender);
        memberDao.updateUserRecord(recordDTO);

        MemberUserDTO memberUserDTO = new MemberUserDTO();
        memberUserDTO.setId(id);
        memberUserDTO.setRealName(realName);
        memberUserDTO.setPhoto(photo);
        memberUserDTO.setCity(address.getCity());
        memberUserDTO.setCounty(address.getCountry());
        memberUserDTO.setProvince(address.getProvince());
        memberUserDTO.setStreet(address.getStreet());
        memberDao.updateUser(memberUserDTO);
    }

    @Override
    public void updateUserRecord(int userId, Float height, Float weight, Float waist, Float bust, Float hip) {
        UserRecordDTO recordDTO = new UserRecordDTO();
        recordDTO.setUserId(userId);
        recordDTO.setHeight(height);
        recordDTO.setWeight(weight);
        recordDTO.setWaist(waist);
        recordDTO.setBust(bust);
        recordDTO.setHip(hip);
        memberDao.updateUserRecord(recordDTO);
    }

    @Override
    public void updateHasWarning(int userId, Integer hasWarning) {
        MemberUserDTO memberUserDTO = new MemberUserDTO();
        memberUserDTO.setId(userId);
        memberUserDTO.setHasWarning(hasWarning);
        memberDao.updateUser(memberUserDTO);
    }


}
